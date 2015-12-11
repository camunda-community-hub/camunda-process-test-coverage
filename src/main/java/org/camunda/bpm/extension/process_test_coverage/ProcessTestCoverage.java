package org.camunda.bpm.extension.process_test_coverage;

import org.apache.commons.io.IOUtils;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageTestRunState;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

public class ProcessTestCoverage {

	private static final Logger log = Logger.getLogger(ProcessTestCoverage.class.getCanonicalName());
	
	@Deprecated
	public static String bpmnDir = "../classes"; // no longer needed as BPMN
													// files are loaded from
													// class path
	public static String targetDir = "target/process-test-coverage";

	private static Map<String, Set<String>> processCoverage = new HashMap<String, Set<String>>();

	private static TestCoverageTestRunState getTestCoverageTestRunState() {
		return TestCoverageTestRunState.INSTANCE(); //XXX
	}

	
	/**
	 * calculate coverage for this, but also add to the overall coverage of the
	 * process
	 */
//	public static void calculate(ProcessInstance processInstance, ProcessEngine processEngine) {
//		calculate(processInstance.getId(), processEngine, getCaller());
//	}

	/**
	 * calculate coverage for this, but also add to the overall coverage of the
	 * process
	 */
//	public static void calculate(String processInstanceId, ProcessEngine processEngine) {
//		calculate(processInstanceId, processEngine, getCaller());
//	}

//	protected static void calculate(String processInstanceId, ProcessEngine processEngine, String caller) {
//		try {
//			HistoricProcessInstance processInstance = getProcessInstance(processInstanceId, processEngine);
//			String bpmnXml = getBpmnXml(processInstance, processEngine);
//			List<String> activities = getAuditTrail(processInstanceId, processEngine);
//
//			// write report for caller
//			String reportName = caller + ".html";
//			BpmnJsReport.highlightActivities(bpmnXml, activities, reportName, targetDir);
//
//			// write report for overall process
//			reportName = getProcessDefinitionKey(processEngine, processInstance) + ".html";
//			Set<String> coveredAcivityIds = callculateProcessCoverage(
//					getProcessDefinitionKey(processEngine, processInstance), activities);
//			BpmnJsReport.highlightActivities(bpmnXml, coveredAcivityIds, reportName, targetDir);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//	}

	/**
	 * calculate overall test coverage for all processes deployed in the engine
	 * 
	 * @return
	 */
	public static Map<String, Coverage> calculate(ProcessEngine processEngine) {
		//FIXME weird: passing a builder leads to concurrency issues
		return calculate(processEngine, /*CoverageBuilder.create("flowNodeAndSequenceFlowCoverage") );// weird */ null, false);
	}
	public static Map<String, Coverage> calculate(ProcessEngine processEngine, CoverageBuilder builder, boolean useDefinitionId) {
		log.info("Calculating coverage");
		try {
			List<ProcessDefinition> processDefinitions = processEngine.getRepositoryService()
					.createProcessDefinitionQuery().list();
			Map<String, Coverage> processesCoverage = new HashMap<String, Coverage>();
			for (ProcessDefinition processDefinition : processDefinitions) {
				BpmnModelInstance modelInstance = processEngine.getRepositoryService()
						.getBpmnModelInstance(processDefinition.getId());
				//
				Collection<FlowNode> flowNodes = modelInstance.getModelElementsByType(FlowNode.class);
				Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);

				String bpmnXml = getBpmnXml(processDefinition);
				
				//processDefinition.getId()
				Set<String> coveredActivityIds = getTestCoverageTestRunState().findCoveredActivityIds();
				String reportName = processDefinition.getKey() + ".html";
				BpmnJsReport.highlightActivities(bpmnXml, coveredActivityIds, reportName, targetDir);

				CoverageBuilder builder2 = builder;
				if (builder2 == null) {
					builder2 = CoverageBuilder.create("flowNodeAndSequenceFlowCoverage");
				}
				builder2
						.forProcess(processDefinition) //
						.withActualFlowNodesAndSequenceFlows(getTestCoverageTestRunState()) //
						.withExpectedFlowNodes(flowNodes) //
						.withExpectedSequenceFlows(sequenceFlows);
				if(useDefinitionId) {
					builder2.filterByDefinitionIdInsteadOfKey();
				}
				Coverage coverage = builder2.build();
				processesCoverage.put(processDefinition.getKey(), coverage);
				log.info("Calculated coverage for " + processDefinition + " : " + coverage);
			}
			return processesCoverage;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	protected static Set<String> callculateProcessCoverage(String processDefinitionKey,
			List<String> activityIds) {
		Set<String> coveredActivites;
		if (!processCoverage.containsKey(processDefinitionKey)) {
			coveredActivites = new HashSet<String>();
			processCoverage.put(processDefinitionKey, coveredActivites);
		} else {
			coveredActivites = processCoverage.get(processDefinitionKey);
		}

		for (String activityId : activityIds) {
			coveredActivites.add(activityId);
		}

		return coveredActivites;
	}

	protected static String getProcessDefinitionKey(ProcessEngine processEngine,
			HistoricProcessInstance processInstance) {
		return processEngine.getRepositoryService().getProcessDefinition(processInstance.getProcessDefinitionId())
				.getKey();
	}

//	protected static String getBpmnXml(HistoricProcessInstance processInstance, ProcessEngine processEngine)
//			throws IOException {
//		String processDefinitionId = processInstance.getProcessDefinitionId();
//		ProcessDefinition processDefinition = processEngine.getRepositoryService().createProcessDefinitionQuery()
//				.processDefinitionId(processDefinitionId).singleResult();
//		return getBpmnXml(processDefinition);
//	}

	protected static String getBpmnXml(ProcessDefinition processDefinition) throws IOException {
		InputStream inputStream = ProcessTestCoverage.class.getClassLoader()
				.getResourceAsStream(processDefinition.getResourceName());
		if (inputStream == null) {
			inputStream = new FileInputStream(processDefinition.getResourceName());
		}
		return IOUtils.toString(inputStream);
	}

//	protected static HistoricProcessInstance getProcessInstance(String processInstanceId, ProcessEngine processEngine) {
//		return processEngine.getHistoryService().createHistoricProcessInstanceQuery()
//				.processInstanceId(processInstanceId).singleResult();
//	}

//	protected static List<String> getAuditTrail(String processInstanceId,
//			ProcessEngine processEngine) {
//		List<HistoricActivityInstance> activities = processEngine.getHistoryService()
//				.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
//		List<String> activityIds = new ArrayList<String>();
//		for(HistoricActivityInstance activity : activities) {
//			activityIds.add(activity.getActivityId());
//		}
//		return activityIds;
//	}

	protected static String getCaller() {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		StackTraceElement caller = stackTraceElements[3];
		String callerMethod = caller.getClassName() + "." + caller.getMethodName();
		return callerMethod;
	}

}