package org.camunda.bpm.extension.process_test_coverage.model;

/**
 * Representation of an executable model.
 *
 * @author dominikhorn
 */
public class Model {

    /**
     * Technical Id of the model
     */
    private final String id;

    /**
     * Key of the Model
     */
    private final String key;

    /**
     * Total count of executable elements
     */
    private final Integer totalElementCount;

    /**
     * Version of the model
     */
    private final String version;

    /**
     * Xml representation of the model
     */
    private final String xml;

    public Model(final String id, final String key, final Integer totalElementCount, final String version, final String xml) {
        this.id = id;
        this.key = key;
        this.totalElementCount = totalElementCount;
        this.version = version;
        this.xml = xml;
    }

    public String getId() {
        return this.id;
    }

    public String getKey() {
        return this.key;
    }

    public Integer getTotalElementCount() {
        return this.totalElementCount;
    }

    public String getVersion() {
        return this.version;
    }

    public String getXml() {
        return this.xml;
    }
}
