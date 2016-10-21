package org.camunda.bpm.spring.boot.starter.example.simple;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.camunda.bpm.spring.boot.starter.example.simple.framework.InMemProcessEngineConfiguration;
import org.camunda.bpm.spring.boot.starter.example.simple.framework.SpringProcessEngineServicesConfiguration;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareAssertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.job;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { SpringProcessWithCoverageTest.TestConfig.class, InMemProcessEngineConfiguration.class,
  SpringProcessEngineServicesConfiguration.class })
public class SpringProcessWithCoverageTest {

  {
    InMemProcessEngineConfiguration.withCoverage = true;
  }

  @Autowired ProcessEngine processEngine;

  @Test
  public void start_and_finish_process() {
    final ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("Sample");

    assertThat(processInstance).isWaitingAt("UserTask_1");

    complete(task());

    assertThat(processInstance).isWaitingAt("ServiceTask_1");
    execute(job());

    assertThat(processInstance).isEnded();
  }


  // for local beans, stubs and mocks
  @Configuration
  public static class TestConfig {

    @Bean SayHelloDelegate sayHelloDelegate() {
      return new SayHelloDelegate();
    }
  }
}
