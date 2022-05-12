package org.camunda.community.process_test_coverage.examples.spring_test.platform8;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeDeployment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeClient
@ZeebeDeployment(resources = "classpath*:*.bpmn")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
