package org.camunda.bpm.spring.boot.starter.example.simple;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.spring.boot.starter.example.simple.framework.InMemProcessEngineConfiguration;
import org.camunda.bpm.spring.boot.starter.example.simple.framework.SpringProcessEngineServicesConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareAssertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { SpringProcessTest.TestConfig.class, InMemProcessEngineConfiguration.class,
  SpringProcessEngineConfiguration.class })
public class SpringProcessTest {

  {
    InMemProcessEngineConfiguration.withCoverage = false;
  }

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
