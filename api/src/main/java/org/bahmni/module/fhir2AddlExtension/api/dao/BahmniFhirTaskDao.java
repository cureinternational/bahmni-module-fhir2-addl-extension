package org.bahmni.module.fhir2AddlExtension.api.dao;

import java.util.List;

import org.openmrs.module.fhir2.api.dao.FhirTaskDao;
import org.openmrs.module.fhir2.model.FhirTask;

public interface BahmniFhirTaskDao extends FhirTaskDao {
	
	FhirTask getTaskByOrderUuid(String orderUuid);
	
	List<FhirTask> getTasksByPatientUuid(String patientUuid, List<String> codeConceptUuids);
}
