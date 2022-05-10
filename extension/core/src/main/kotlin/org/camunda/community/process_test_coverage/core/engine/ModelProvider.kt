package org.camunda.community.process_test_coverage.core.engine

import org.camunda.community.process_test_coverage.core.model.Model

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