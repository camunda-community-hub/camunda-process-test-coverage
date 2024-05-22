package org.camunda.community.process_test_coverage.examples.spring_starter.platform7;

/*-
 * #%L
 * Camunda Process Test Coverage Example Spring-Testing with starter Platform 7
 * %%
 * Copyright (C) 2019 - 2024 Camunda
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
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
 * #L%
 */

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
