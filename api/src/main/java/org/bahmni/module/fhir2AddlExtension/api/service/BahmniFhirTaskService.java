package org.bahmni.module.fhir2AddlExtension.api.service;

import java.util.List;

import org.hl7.fhir.r4.model.Task;
import org.openmrs.module.fhir2.api.FhirTaskService;

public interface BahmniFhirTaskService extends FhirTaskService {
	
	List<Task> getTasksByPatientUuid(String patientUuid, List<String> codeConceptUuids);
}
