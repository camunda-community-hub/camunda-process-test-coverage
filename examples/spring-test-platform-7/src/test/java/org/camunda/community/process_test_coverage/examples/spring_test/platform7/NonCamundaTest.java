package org.camunda.community.process_test_coverage.examples.spring_test.platform7;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class NonCamundaTest {

    @Test
    public void testSomethingElseAgain() {
        Assertions.assertTrue(true);
    }


}
