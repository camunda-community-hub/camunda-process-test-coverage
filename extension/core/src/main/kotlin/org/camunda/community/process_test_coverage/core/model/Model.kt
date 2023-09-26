package org.camunda.community.process_test_coverage.core.model

/**
 * Representation of an executable model.
 *
 * @author dominikhorn
 */
data class Model(

    /**
     * Key of the model.
     */
    val key: String,
    /**
     * Total count of executable elements.
     */
    val totalElementCount: Int,
    /**
     * Version of the model.
     */
    val version: String?,
    /**
     * XML representation of the model.
     */
    val xml: String
)