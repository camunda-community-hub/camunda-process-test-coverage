package org.camunda.bpm.extension.process_test_coverage;

import java.util.List;

import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.junit.runner.Description;

public class TestCoverageProcessEngineRule extends ProcessEngineRule {

	public static enum TypeIncludedInCoverage {
	
		FlowElement(org.camunda.bpm.model.bpmn.instance.FlowElement.class);
		
		private Class<? extends FlowElement> coveredTypeClass;
		
		private TypeIncludedInCoverage(final Class<? extends FlowElement> coveredTypeClass) {
			this.coveredTypeClass = coveredTypeClass;
		}
		
		public Class<? extends FlowElement> getCoveredTypeClass() {
			return coveredTypeClass;
		}
		
	};
	
	private int numberOfPossibleElements;
	
	@Override
	public void starting(Description description) {

		// run derived functionality
		super.starting(description);

	}

	@Override
	public void finished(Description description) {

	    // calculate coverage for all tests
	    ProcessTestCoverage.calculate(processEngine);

		// calculate possible coverage
		
		if (this.deploymentId != null) {

			numberOfPossibleElements = 0;
			
			final List<String> deploymentResourceNames = repositoryService
					.getDeploymentResourceNames(this.deploymentId);
			if (deploymentResourceNames != null) {

				for (final String deploymentResourceName : deploymentResourceNames) {

					final List<ProcessDefinition> processDefinitions =
							repositoryService.createProcessDefinitionQuery()
							.processDefinitionResourceName(deploymentResourceName).list();
					for (final ProcessDefinition processDefinition : processDefinitions) {
						
						final BpmnModelInstance bpmnModelInstance = repositoryService
								.getBpmnModelInstance(processDefinition.getId());
						
						for (final TypeIncludedInCoverage type : TypeIncludedInCoverage.values()) {
							numberOfPossibleElements += bpmnModelInstance
									.getModelElementsByType(type.getCoveredTypeClass()).size();
						}
						
					}

				}

			}
			
			System.out.println(numberOfPossibleElements);

		}
		
		// run derived functionality
		super.finished(description);

	}

}
