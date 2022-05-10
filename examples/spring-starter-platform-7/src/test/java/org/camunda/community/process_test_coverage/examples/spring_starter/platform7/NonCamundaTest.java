package org.camunda.community.process_test_coverage.examples.spring_starter.platform7;

import org.camunda.community.process_test_coverage.core.engine.ExcludeFromProcessCoverage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExcludeFromProcessCoverage
public class NonCamundaTest {

    @Test
    public void testSomethingElseAgain() {
        Assertions.assertTrue(true);
    }


}
