package org.bahmni.module.fhir2AddlExtension.api.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bahmni.module.fhir2AddlExtension.api.dao.BahmniFhirTaskDao;
import org.hl7.fhir.r4.model.Task;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openmrs.module.fhir2.api.translators.TaskTranslator;
import org.openmrs.module.fhir2.model.FhirTask;

@RunWith(MockitoJUnitRunner.class)
public class BahmniFhirTaskServiceImplTest {
	
	private static final String TASK_UUID = "task-uuid-456";
	
	@Mock
	private BahmniFhirTaskDao dao;
	
	@Mock
	private TaskTranslator translator;
	
	private BahmniFhirTaskServiceImpl taskService;
	
	@Before
	public void setup() {
		taskService = new BahmniFhirTaskServiceImpl() {
			
			@Override
			protected void validateObject(FhirTask object) {
			}
		};
		taskService.setDao(dao);
		taskService.setTranslator(translator);
	}
	
	@Test
	public void create_shouldTranslateAndDelegateToDao() {
		Task inputTask = new Task();
		FhirTask openmrsTask = new FhirTask();
		openmrsTask.setUuid(TASK_UUID);
		FhirTask savedTask = new FhirTask();
		savedTask.setUuid(TASK_UUID);
		Task expectedResult = new Task();
		expectedResult.setId(TASK_UUID);
		
		when(translator.toOpenmrsType(inputTask)).thenReturn(openmrsTask);
		when(dao.createOrUpdate(openmrsTask)).thenReturn(savedTask);
		when(translator.toFhirResource(savedTask)).thenReturn(expectedResult);
		
		Task result = taskService.create(inputTask);
		
		assertThat(result, notNullValue());
		assertThat(result.getId(), equalTo(TASK_UUID));
		verify(translator).toOpenmrsType(inputTask);
		verify(dao).createOrUpdate(openmrsTask);
		verify(translator).toFhirResource(savedTask);
	}
	
	@Test
	public void getTasksByPatientUuid_shouldReturnTranslatedTasksForPatient() {
		String patientUuid = "patient-uuid-abc";
		List<String> codeUuids = Arrays.asList("concept-uuid-1");
		FhirTask fhirTask1 = new FhirTask();
		fhirTask1.setUuid("fhir-task-uuid-1");
		FhirTask fhirTask2 = new FhirTask();
		fhirTask2.setUuid("fhir-task-uuid-2");
		Task task1 = new Task();
		task1.setId("task-1");
		Task task2 = new Task();
		task2.setId("task-2");
		
		when(dao.getTasksByPatientUuid(patientUuid, codeUuids)).thenReturn(Arrays.asList(fhirTask1, fhirTask2));
		when(translator.toFhirResource(same(fhirTask1))).thenReturn(task1);
		when(translator.toFhirResource(same(fhirTask2))).thenReturn(task2);
		
		List<Task> result = taskService.getTasksByPatientUuid(patientUuid, codeUuids);
		
		assertThat(result, notNullValue());
		assertThat(result, hasSize(2));
		assertThat(result.get(0).getId(), equalTo("task-1"));
		assertThat(result.get(1).getId(), equalTo("task-2"));
		verify(dao).getTasksByPatientUuid(patientUuid, codeUuids);
		verify(translator).toFhirResource(same(fhirTask1));
		verify(translator).toFhirResource(same(fhirTask2));
	}
	
	@Test
	public void getTasksByPatientUuid_shouldReturnEmptyListWhenNoTasksFound() {
		String patientUuid = "patient-uuid-abc";
		
		when(dao.getTasksByPatientUuid(patientUuid, Collections.emptyList())).thenReturn(Collections.emptyList());
		
		List<Task> result = taskService.getTasksByPatientUuid(patientUuid, Collections.emptyList());
		
		assertThat(result, notNullValue());
		assertThat(result, hasSize(0));
	}
	
	@Test
	public void get_shouldDelegateToDao() {
		FhirTask openmrsTask = new FhirTask();
		openmrsTask.setUuid(TASK_UUID);
		Task expectedTask = new Task();
		expectedTask.setId(TASK_UUID);
		
		when(dao.get(TASK_UUID)).thenReturn(openmrsTask);
		when(translator.toFhirResource(openmrsTask)).thenReturn(expectedTask);
		
		Task result = taskService.get(TASK_UUID);
		
		assertThat(result, notNullValue());
		assertThat(result.getId(), equalTo(TASK_UUID));
		verify(dao).get(TASK_UUID);
	}
}
