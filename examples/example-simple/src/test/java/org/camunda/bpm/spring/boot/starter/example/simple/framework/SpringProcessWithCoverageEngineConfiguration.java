package org.camunda.bpm.spring.boot.starter.example.simple.framework;

import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.extension.process_test_coverage.listeners.CompensationEventCoverageHandler;
import org.camunda.bpm.extension.process_test_coverage.listeners.FlowNodeHistoryEventHandler;
import org.camunda.bpm.extension.process_test_coverage.listeners.PathCoverageParseListener;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by wql on 20-10-2016.
 * Body copied from https://github.com/camunda/camunda-bpm-process-test-coverage/blob/master/core/src/main/java/org/camunda/bpm/extension/process_test_coverage/junit/rules/ProcessCoverageInMemProcessEngineConfiguration.java
 */
public class SpringProcessWithCoverageEngineConfiguration extends SpringProcessEngineConfiguration {

  protected void init(boolean withCoverage) {
    if (withCoverage) {
      this.initializeFlowNodeHandler();
      this.initializePathCoverageParseListener();
      this.initializeCompensationEventHandler();
    }
    super.init();
  }

  private void initializePathCoverageParseListener() {
    Object bpmnParseListeners = this.getCustomPostBPMNParseListeners();
    if(bpmnParseListeners == null) {
      bpmnParseListeners = new LinkedList();
      this.setCustomPostBPMNParseListeners((List)bpmnParseListeners);
    }

    ((List)bpmnParseListeners).add(new PathCoverageParseListener());
  }

  private void initializeFlowNodeHandler() {
    FlowNodeHistoryEventHandler historyEventHandler = new FlowNodeHistoryEventHandler();
    this.setHistoryEventHandler(historyEventHandler);
  }

  private void initializeCompensationEventHandler() {
    if(this.getCustomEventHandlers() == null) {
      this.setCustomEventHandlers(new LinkedList());
    }

    this.customEventHandlers.add(new CompensationEventCoverageHandler());
  }


}
