package org.camunda.community.process_test_coverage.junit4.platform7.spring;

import java.lang.reflect.Method;

import javax.sql.DataSource;

import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.engine.spring.ProcessEngineFactoryBean;
import org.camunda.bpm.engine.spring.SpringExpressionManager;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.community.process_test_coverage.junit4.platform7.spring.SpringProcessWithCoverageEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Adapted from: https://github.com/camunda/camunda-bpm-platform/blob/master/engine-spring/src/test/java/org/camunda/bpm/engine/spring/test/configuration/InMemProcessEngineConfiguration.java
 */
@Configuration
public class InMemProcessEngineConfiguration {

  @Autowired
  ApplicationContext applicationContext;

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

  @Bean
  public ProcessEngineConfigurationImpl processEngineConfiguration() throws Exception {

    SpringProcessWithCoverageEngineConfiguration config = new SpringProcessWithCoverageEngineConfiguration();

    
    try {
      Method setApplicationContext = SpringProcessEngineConfiguration.class.getDeclaredMethod("setApplicationContext", ApplicationContext.class);
      if (setApplicationContext != null) {
        setApplicationContext.invoke(config, applicationContext);
      }
    } catch (NoSuchMethodException e) {
      // expected for Camunda < 7.8.0
    }
    config.setExpressionManager(expressionManager());
    config.setTransactionManager(transactionManager());
    config.setDataSource(dataSource());
    config.setDatabaseSchemaUpdate("true");
    config.setHistory(ProcessEngineConfiguration.HISTORY_FULL);
    config.setJobExecutorActivate(false);
    config.init();
    return config;

  }

  @Bean
  ExpressionManager expressionManager() {

    return new SpringExpressionManager(applicationContext, null);

  }

  @Bean
  public ProcessEngineFactoryBean processEngine() throws Exception {

    ProcessEngineFactoryBean factoryBean = new ProcessEngineFactoryBean();
    factoryBean.setProcessEngineConfiguration(processEngineConfiguration());
    return factoryBean;

  }

}
