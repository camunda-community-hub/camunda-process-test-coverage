package org.camunda.bpm.extension.process_test_coverage.engine

import org.camunda.bpm.extension.process_test_coverage.model.Model

/**
 * Provider for process model.
 */
interface ModelProvider {
    /**
     * Retrieves a model by key.
     * @param key process definition key
     * @return model.
     */
    fun getModel(key: String): Model
}