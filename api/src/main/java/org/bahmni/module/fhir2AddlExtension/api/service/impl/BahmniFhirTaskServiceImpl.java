package org.bahmni.module.fhir2AddlExtension.api.service.impl;

import ca.uhn.fhir.rest.api.server.IBundleProvider;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bahmni.module.fhir2AddlExtension.api.dao.BahmniFhirTaskDao;
import org.bahmni.module.fhir2AddlExtension.api.service.BahmniFhirTaskService;
import org.openmrs.module.fhir2.api.dao.FhirTaskDao;
import org.openmrs.module.fhir2.api.impl.BaseFhirService;
import org.openmrs.module.fhir2.api.search.SearchQuery;
import org.openmrs.module.fhir2.api.search.SearchQueryInclude;
import org.openmrs.module.fhir2.api.search.param.TaskSearchParams;
import org.openmrs.module.fhir2.api.translators.TaskTranslator;
import org.openmrs.module.fhir2.model.FhirTask;
import org.hl7.fhir.r4.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class BahmniFhirTaskServiceImpl extends BaseFhirService<Task, FhirTask> implements BahmniFhirTaskService {
	
	@Getter(value = AccessLevel.PROTECTED)
	@Setter(value = AccessLevel.PACKAGE, onMethod_ = @Autowired)
	private BahmniFhirTaskDao dao;
	
	@Getter(value = AccessLevel.PROTECTED)
	@Setter(value = AccessLevel.PACKAGE, onMethod_ = @Autowired)
	private TaskTranslator translator;
	
	@Setter(value = AccessLevel.PACKAGE, onMethod_ = @Autowired)
	private SearchQueryInclude<Task> searchQueryInclude;
	
	@Setter(value = AccessLevel.PACKAGE, onMethod_ = @Autowired)
	private SearchQuery<FhirTask, Task, FhirTaskDao, TaskTranslator, SearchQueryInclude<Task>> searchQuery;
	
	@Override
	public IBundleProvider searchForTasks(TaskSearchParams taskSearchParams) {
		return searchQuery.getQueryResults(taskSearchParams.toSearchParameterMap(), dao, translator, searchQueryInclude);
	}
	
	@Override
	public List<org.hl7.fhir.r4.model.Task> getTasksByPatientUuid(String patientUuid, List<String> codeConceptUuids) {
		return dao.getTasksByPatientUuid(patientUuid, codeConceptUuids).stream()
		        .map(translator::toFhirResource)
		        .collect(Collectors.toList());
	}
}
