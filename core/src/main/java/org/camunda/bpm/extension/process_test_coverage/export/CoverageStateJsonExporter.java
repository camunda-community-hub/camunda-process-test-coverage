package org.camunda.bpm.extension.process_test_coverage.export;

import camundajar.impl.com.google.gson.Gson;
import org.camunda.bpm.extension.process_test_coverage.model.Model;
import org.camunda.bpm.extension.process_test_coverage.model.Suite;

import java.util.Collection;

/**
 * Exporter for Coverage State
 *
 * @author dominikhorn
 */
public class CoverageStateJsonExporter {

    /**
     * This Methods creates a Json String from the given input.
     *
     * @param suites Suites that should be exported
     * @param models Models that should be exported
     * @return
     */
    public static String createCoverageStateResult(final Collection<Suite> suites, final Collection<Model> models) {
        final CoverageStateResult result = new CoverageStateResult(suites, models);
        final Gson gson = new Gson();
        return gson.toJson(result);
    }

}
