/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.spring.boot.starter.example.simple.framework;

import javax.sql.DataSource;

import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.engine.spring.ProcessEngineFactoryBean;
import org.camunda.bpm.engine.spring.SpringExpressionManager;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.ProcessCoverageInMemProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

/**
 * Adapted from:
 * https://github.com/camunda/camunda-bpm-platform/blob/master/engine-spring/src/test/java/org/camunda/bpm/engine/spring/test/configuration/InMemProcessEngineConfiguration.java
 *
 * Base Java Config for the process engine that uses In-Memory database.
 *
 * @author Philipp Ossler
 */
@Configuration
public class InMemProcessEngineConfiguration {
  // just a quick and dirty hack to make the code work two ways
  public static boolean withCoverage = false;

  @Bean
  public DataSource dataSource() {
    SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
    dataSource.setDriverClass(org.h2.Driver.class);
    dataSource.setUrl("jdbc:h2:mem:camunda-test;DB_CLOSE_DELAY=-1");
    dataSource.setUsername("sa");
    dataSource.setPassword("");
    return dataSource;
  }

  @Bean
  public PlatformTransactionManager transactionManager() {
    return new DataSourceTransactionManager(dataSource());
  }

  @Autowired
  private ResourcePatternResolver resourceLoader;

  @Bean
  public ProcessEngineConfigurationImpl processEngineConfiguration() throws IOException {
    // SpringProcessEngineConfiguration configs = new SpringProcessEngineConfiguration();
    SpringProcessWithCoverageEngineConfiguration config = new SpringProcessWithCoverageEngineConfiguration();
    config.setExpressionManager(expressionManager());
    config.setTransactionManager(transactionManager());

    config.setProcessEnginePlugins(Arrays.asList(
        new org.camunda.spin.plugin.impl.SpinProcessEnginePlugin(),
        new org.camunda.connect.plugin.impl.ConnectProcessEnginePlugin()
    ));

    config.setDataSource(dataSource());
    config.setDatabaseSchemaUpdate("true");

    // config.setTransactionManager(transactionManager());

    config.setHistory(ProcessEngineConfiguration.HISTORY_FULL);
    config.setJobExecutorActivate(false);
    config.setDbMetricsReporterActivate(false);

    // deploy all processes from folder 'processes'
    Resource[] resources = resourceLoader.getResources("classpath:/bpmn/*.bpmn");
    config.setDeploymentResources(resources);

    config.init(withCoverage);

    return config;
  }

  @Autowired ApplicationContext applicationContext;

  @Bean
  ExpressionManager expressionManager() {
    return new SpringExpressionManager(applicationContext, null);
  }

  @Bean
  public ProcessEngineFactoryBean processEngine() throws IOException {
    ProcessEngineFactoryBean factoryBean = new ProcessEngineFactoryBean();
    factoryBean.setProcessEngineConfiguration(processEngineConfiguration());
    return factoryBean;
  }

}
