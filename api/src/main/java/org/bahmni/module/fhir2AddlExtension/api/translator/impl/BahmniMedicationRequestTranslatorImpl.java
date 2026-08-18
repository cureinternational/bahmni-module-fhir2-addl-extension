package org.bahmni.module.fhir2AddlExtension.api.translator.impl;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bahmni.module.fhir2AddlExtension.api.BahmniFhirConstants;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.StringType;
import org.openmrs.CareSetting;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.api.OrderService;
import org.openmrs.module.fhir2.api.translators.impl.MedicationRequestTranslatorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Objects;

@Component
@Primary
@Slf4j
public class BahmniMedicationRequestTranslatorImpl extends MedicationRequestTranslatorImpl {
	
	@Autowired
	@Setter(value = AccessLevel.PACKAGE)
	private OrderService orderService;
	
	@Override
	public MedicationRequest toFhirResource(@Nonnull DrugOrder drugOrder) {
		MedicationRequest medicationRequest = super.toFhirResource(drugOrder);
		getAdmissionStatus(drugOrder).ifPresent(admissionStatus -> medicationRequest.addExtension(
		    BahmniFhirConstants.FHIR_EXT_MEDICATION_REQUEST_ADMISSION_STATUS, new StringType(admissionStatus)));
		return medicationRequest;
	}
	
	private java.util.Optional<String> getAdmissionStatus(DrugOrder drugOrder) {
		try {
			if (drugOrder.getEncounter() == null || drugOrder.getEncounter().getVisit() == null) {
				return java.util.Optional.empty();
			}
			Visit visit = drugOrder.getEncounter().getVisit();
			if (visit.getActiveAttributes() == null) {
				return java.util.Optional.empty();
			}
			return visit.getActiveAttributes().stream().filter(Objects::nonNull)
			        .filter(attribute -> attribute.getAttributeType() != null)
			        .filter(attribute -> BahmniFhirConstants.ADMISSION_STATUS_VISIT_ATTRIBUTE_TYPE_NAME
			                .equals(attribute.getAttributeType().getName()))
			        .map(VisitAttribute::getValueReference).filter(Objects::nonNull).findFirst();
		}
		catch (Exception e) {
			log.warn("Could not resolve admission status for drug order {}: {}", drugOrder.getUuid(), e.getMessage());
			return java.util.Optional.empty();
		}
	}
	
	@Override
	public DrugOrder toOpenmrsType(@Nonnull DrugOrder existingDrugOrder, @Nonnull MedicationRequest medicationRequest) {
		DrugOrder drugOrder = super.toOpenmrsType(existingDrugOrder, medicationRequest);
		
		//TODO: This should be translated based on an extension of MedicationRequest to set correct CareSetting
		drugOrder.setCareSetting(orderService.getCareSettingByName(CareSetting.CareSettingType.OUTPATIENT.name()));
		
		if (drugOrder.getUrgency() != null && drugOrder.getUrgency().equals(Order.Urgency.STAT)) {
			drugOrder.setScheduledDate(null);
		} else if (drugOrder.getScheduledDate() != null) {
			drugOrder.setUrgency(Order.Urgency.ON_SCHEDULED_DATE);
		}
		return drugOrder;
	}
}
