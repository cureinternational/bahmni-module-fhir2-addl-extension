package org.bahmni.module.fhir2AddlExtension.api.providers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bahmni.module.fhir2AddlExtension.api.service.BahmniFhirTaskService;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Task;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;

@RunWith(MockitoJUnitRunner.class)
public class BahmniTaskFhirR4ResourceProviderTest {
	
	private static final String PATIENT_UUID = "68b4fad9-3c2d-4174-a0c5-61c09099d681";
	
	private static final String FORM_APPROVAL_UUID = "4b1d1aad-859b-405c-ac81-75d5201d0c86";
	
	private static final String FORM_COMMENT_UUID = "c1683063-bc46-4c09-a95d-5b961f544d13";
	
	@Mock
	private BahmniFhirTaskService bahmniFhirTaskService;
	
	@InjectMocks
	private BahmniTaskFhirR4ResourceProvider provider;
	
	@Before
	public void setup() {
		when(bahmniFhirTaskService.getTasksByPatientUuid(PATIENT_UUID, Collections.emptyList())).thenReturn(
		    Collections.emptyList());
	}
	
	@Test
	public void getPatientTasks_shouldReturnSearchsetBundleForValidPatient() {
		Task task = new Task();
		task.setId("task-uuid-1");
		when(bahmniFhirTaskService.getTasksByPatientUuid(PATIENT_UUID, Collections.emptyList())).thenReturn(
		    Collections.singletonList(task));
		
		Bundle result = provider.getPatientTasks(new StringType(PATIENT_UUID), null);
		
		assertThat(result, notNullValue());
		assertThat(result.getType(), equalTo(Bundle.BundleType.SEARCHSET));
		assertThat(result.getTotal(), equalTo(1));
		assertThat(result.getEntry().get(0).getResource().getId(), equalTo("task-uuid-1"));
	}
	
	@Test
	public void getPatientTasks_shouldDelegateToServiceWithEmptyCodesWhenCodesNotProvided() {
		provider.getPatientTasks(new StringType(PATIENT_UUID), null);
		
		verify(bahmniFhirTaskService).getTasksByPatientUuid(PATIENT_UUID, Collections.emptyList());
	}
	
	@Test
	public void getPatientTasks_shouldPassCodeValuesToService() {
		List<StringType> codes = Arrays.asList(new StringType(FORM_APPROVAL_UUID), new StringType(FORM_COMMENT_UUID));
		List<String> expectedCodeValues = Arrays.asList(FORM_APPROVAL_UUID, FORM_COMMENT_UUID);
		when(bahmniFhirTaskService.getTasksByPatientUuid(PATIENT_UUID, expectedCodeValues)).thenReturn(
		    Collections.emptyList());
		
		provider.getPatientTasks(new StringType(PATIENT_UUID), codes);
		
		verify(bahmniFhirTaskService).getTasksByPatientUuid(PATIENT_UUID, expectedCodeValues);
	}
	
	@Test
	public void getPatientTasks_shouldReturnEmptyBundleWhenNoTasksFound() {
		when(bahmniFhirTaskService.getTasksByPatientUuid(PATIENT_UUID, Collections.emptyList())).thenReturn(
		    Collections.emptyList());
		
		Bundle result = provider.getPatientTasks(new StringType(PATIENT_UUID), null);
		
		assertThat(result.getTotal(), equalTo(0));
		assertThat(result.getEntry().size(), equalTo(0));
	}
	
	@Test
	public void getPatientTasks_shouldReturnAllMatchingTasksAsEntries() {
		Task task1 = new Task();
		task1.setId("task-uuid-1");
		Task task2 = new Task();
		task2.setId("task-uuid-2");
		when(bahmniFhirTaskService.getTasksByPatientUuid(PATIENT_UUID, Collections.emptyList()))
		        .thenReturn(Arrays.asList(task1, task2));

		Bundle result = provider.getPatientTasks(new StringType(PATIENT_UUID), null);

		assertThat(result.getTotal(), equalTo(2));
		List<String> entryIds = result.getEntry().stream()
		        .map(e -> e.getResource().getId())
		        .collect(Collectors.toList());
		assertThat(entryIds, containsInAnyOrder("task-uuid-1", "task-uuid-2"));
	}
	
	@Test
	public void getPatientTasks_shouldThrowInvalidRequestExceptionWhenPatientUuidIsNull() {
		assertThrows(InvalidRequestException.class, () -> provider.getPatientTasks(null, null));
	}
	
	@Test
	public void getPatientTasks_shouldThrowInvalidRequestExceptionWhenPatientUuidIsBlank() {
		assertThrows(InvalidRequestException.class,
		    () -> provider.getPatientTasks(new StringType("  "), null));
	}
}
