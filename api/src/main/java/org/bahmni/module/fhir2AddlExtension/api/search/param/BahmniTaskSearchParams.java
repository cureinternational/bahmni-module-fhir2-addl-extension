package org.bahmni.module.fhir2AddlExtension.api.search.param;

import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bahmni.module.fhir2AddlExtension.api.BahmniFhirConstants;
import org.openmrs.module.fhir2.FhirConstants;
import org.openmrs.module.fhir2.api.search.param.SearchParameterMap;
import org.openmrs.module.fhir2.api.search.param.TaskSearchParams;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BahmniTaskSearchParams extends TaskSearchParams {
	
	private ReferenceAndListParam encounterReference;
	
	private StringAndListParam formName;
	
	@Override
	public SearchParameterMap toSearchParameterMap() {
		SearchParameterMap map = super.toSearchParameterMap();
		if (encounterReference != null) {
			map.addParameter(FhirConstants.ENCOUNTER_REFERENCE_SEARCH_HANDLER, encounterReference);
		}
		if (formName != null) {
			map.addParameter(BahmniFhirConstants.FORM_NAME_SEARCH_HANDLER, formName);
		}
		return map;
	}
}
