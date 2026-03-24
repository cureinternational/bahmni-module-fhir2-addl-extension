package org.bahmni.module.fhir2AddlExtension.api.translator.impl;

import javax.annotation.Nonnull;

import org.hl7.fhir.r4.model.Task;
import org.openmrs.module.fhir2.api.translators.impl.TaskTranslatorImpl;
import org.openmrs.module.fhir2.model.FhirTask;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class BahmniTaskTranslatorImpl extends TaskTranslatorImpl {
	
	@Override
	public Task toFhirResource(@Nonnull FhirTask fhirTask) {
		Task task = super.toFhirResource(fhirTask);
		String name = fhirTask.getName();
		// Auto-generated names follow the "ResourceType/uuid" pattern — skip those
		if (name != null && !name.contains("/")) {
			task.setDescription(name);
		}
		return task;
	}
	
	@Override
	public FhirTask toOpenmrsType(@Nonnull Task task) {
		FhirTask fhirTask = super.toOpenmrsType(task);
		if (task.hasDescription()) {
			fhirTask.setName(task.getDescription());
		}
		return fhirTask;
	}
	
	@Override
	public FhirTask toOpenmrsType(@Nonnull FhirTask existingTask, @Nonnull Task task) {
		FhirTask fhirTask = super.toOpenmrsType(existingTask, task);
		if (task.hasDescription()) {
			fhirTask.setName(task.getDescription());
		}
		return fhirTask;
	}
}
