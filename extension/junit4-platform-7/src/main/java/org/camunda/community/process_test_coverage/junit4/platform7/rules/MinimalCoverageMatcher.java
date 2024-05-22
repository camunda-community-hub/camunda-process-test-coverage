package org.camunda.community.process_test_coverage.junit4.platform7.rules;

/*-
 * #%L
 * Camunda Process Test Coverage JUnit4 Platform 7
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

import org.camunda.community.process_test_coverage.core.model.Coverage;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class MinimalCoverageMatcher extends BaseMatcher<Double> {

    double minimalCoverage;

    public MinimalCoverageMatcher(final double minimalCoveragePercentage) {
        if (0 > minimalCoveragePercentage || minimalCoveragePercentage > 1) {
            throw new RuntimeException("ILLEGAL TEST CONFIGURATION: minimal coverage percentage must be between 0.0 and 1.0 (was " + minimalCoveragePercentage + ")");
        }
        this.minimalCoverage = minimalCoveragePercentage;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("matches if the coverage ratio is at least ").appendValue(this.minimalCoverage);
    }

    @Override
    public boolean matches(final Object item) {
        return this.actualPercentage(item) >= this.minimalCoverage;
    }

    private double actualPercentage(final Object item) {
        if (item instanceof Number) {
            return (double) ((Number) item).doubleValue();
        }
        return -1.0;
    }

    @Override
    public void describeMismatch(final Object item, final Description mismatchDescription) {
        if (item instanceof Number || item instanceof Coverage) {
            mismatchDescription.appendText("coverage of ").appendValue(this.actualPercentage(item));
            mismatchDescription.appendText(" is too low)");
            // TODO describe diff of actual and expected items
        } else {
            mismatchDescription.appendValue(item).appendText("is not a Number or Coverage");
        }

    }

}
