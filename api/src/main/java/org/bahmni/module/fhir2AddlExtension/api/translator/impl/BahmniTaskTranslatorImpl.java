package org.bahmni.module.fhir2AddlExtension.api.translator.impl;

import javax.annotation.Nonnull;

import org.bahmni.module.fhir2AddlExtension.api.BahmniFhirConstants;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Task;
import org.openmrs.Provider;
import org.openmrs.module.fhir2.api.translators.PractitionerReferenceTranslator;
import org.openmrs.module.fhir2.api.translators.impl.TaskTranslatorImpl;
import org.openmrs.module.fhir2.model.FhirTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class BahmniTaskTranslatorImpl extends TaskTranslatorImpl {
	
	@Autowired
	private PractitionerReferenceTranslator<Provider> providerReferenceTranslator;
	
	@Override
	public Task toFhirResource(@Nonnull FhirTask fhirTask) {
		Task task = super.toFhirResource(fhirTask);
		
		// Map fhir_task.name → extension, but skip auto-generated names like "Task/{uuid}"
		String name = fhirTask.getName();
		if (name != null && !name.contains("/")) {
			task.addExtension(BahmniFhirConstants.FHIR_EXT_TASK_FORM_NAME, new StringType(name));
		}
		
		if (fhirTask.getOwnerReference() != null) {
			Reference ownerRef = new Reference();
			ownerRef.setReference(fhirTask.getOwnerReference().getReference());
			ownerRef.setType(fhirTask.getOwnerReference().getType());
			Provider owner = providerReferenceTranslator.toOpenmrsType(ownerRef);
			if (owner != null && owner.getName() != null) {
				if (!task.hasOwner()) {
					task.setOwner(ownerRef);
				}
				task.getOwner().setDisplay(owner.getName());
			}
		}
		
		return task;
	}
	
	@Override
	public FhirTask toOpenmrsType(@Nonnull Task task) {
		FhirTask fhirTask = super.toOpenmrsType(task);
		Extension formNameExt = task.getExtensionByUrl(BahmniFhirConstants.FHIR_EXT_TASK_FORM_NAME);
		if (formNameExt != null && formNameExt.getValue() instanceof StringType) {
			fhirTask.setName(((StringType) formNameExt.getValue()).getValue());
		}
		return fhirTask;
	}
	
	@Override
	public FhirTask toOpenmrsType(@Nonnull FhirTask existingTask, @Nonnull Task task) {
		FhirTask fhirTask = super.toOpenmrsType(existingTask, task);
		Extension formNameExt = task.getExtensionByUrl(BahmniFhirConstants.FHIR_EXT_TASK_FORM_NAME);
		if (formNameExt != null && formNameExt.getValue() instanceof StringType) {
			fhirTask.setName(((StringType) formNameExt.getValue()).getValue());
		}
		return fhirTask;
	}
}
