package org.camunda.bpm.extension.process_test_coverage.model

/**
 * Representation of an executable model.
 *
 * @author dominikhorn
 */
class Model(

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