package org.camunda.bpm.extension.process_test_coverage.model

import org.camunda.bpm.extension.process_test_coverage.engine.ModelProvider

/**
 * Default Collector for the coverage.
 *
 * @author dominikhorn
 */
class DefaultCollector : Collector {

    private val excludedProcessDefinitionKeys: MutableList<String> = mutableListOf()
    private val suites: MutableMap<String, Suite> = hashMapOf()
    private val models: MutableMap<String, Model> = hashMapOf()
    private val modelProvider = ModelProvider()

    var activeRun: Run? = null
        private set
    var activeSuite: Suite? = null
        private set

    override fun addEvent(event: Event) {
        requireNotNull(activeRun) { "No active suite available" }

        if (excludedProcessDefinitionKeys.contains(event.modelKey)) {
            return
        }
        saveModel(event)
        activeRun!!.addEvent(event)
    }

    fun createSuite(suite: Suite): Suite {
        require(!suites.containsKey(suite.id)) { "Suite already exists" }
        suites[suite.id] = suite
        return suite
    }

    fun createRun(run: Run, suiteId: String): Run {
        getSuite(suiteId).addRun(run)
        return run
    }

    fun activateSuite(suiteId: String) {
        val suite = getSuite(suiteId)
        activeSuite = suite
    }

    fun activateRun(runId: String) {
        requireNotNull(activeSuite) { "No active suite available" }
        val run = activeSuite!!.getRun(runId) ?: throw IllegalArgumentException("Run doesn't exist")
        activeRun = run
    }

    fun setExcludedProcessDefinitionKeys(excludedProcessDefinitionKeys: List<String>) {
        this.excludedProcessDefinitionKeys.clear()
        this.excludedProcessDefinitionKeys.addAll(excludedProcessDefinitionKeys)
    }

    private fun getSuite(suiteId: String): Suite {
        require(suites.containsKey(suiteId)) { "Suite with id $suiteId doesn't exist" }
        return suites.getValue(suiteId)
    }

    private fun saveModel(event: Event) {
        if (models.containsKey(event.modelKey)) {
            return
        }
        val model = modelProvider.getModel(event.modelKey)
        models[model.key] = model
    }

    fun getModels(): Collection<Model> {
        return models.values
    }

    fun getSuites(): Map<String, Suite> {
        return suites
    }
}