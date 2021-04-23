package org.camunda.bpm.extension.process_test_coverage.model

import org.camunda.bpm.extension.process_test_coverage.engine.ModelProvider

/**
 * Default Collector for the coverage.
 *
 * @author dominikhorn
 */
class DefaultCollector(private val modelProvider: ModelProvider) : Collector {

    private val excludedProcessDefinitionKeys: MutableList<String> = mutableListOf()
    private val suites: MutableMap<String, Suite> = hashMapOf()
    private val models: MutableMap<String, Model> = hashMapOf()

    lateinit var activeRun: Run
    lateinit var activeSuite: Suite

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
        activeSuite = getSuite(suiteId)
    }

    fun activateRun(runId: String) {
        require(this::activeSuite.isInitialized) { "No active suite available" }
        activeRun = activeSuite.getRun(runId) ?: throw IllegalArgumentException("Run $runId doesn't exist in suite ${activeSuite.id}")
    }

    override fun addEvent(event: Event) {
        require(this::activeRun.isInitialized) { "No active run available" }

        if (excludedProcessDefinitionKeys.contains(event.modelKey)) {
            return
        }
        addModelIfMissing(event)
        activeRun.addEvent(event)
    }

    fun setExcludedProcessDefinitionKeys(excludedProcessDefinitionKeys: List<String>) {
        this.excludedProcessDefinitionKeys.clear()
        this.excludedProcessDefinitionKeys.addAll(excludedProcessDefinitionKeys)
    }

    fun getModels(): Collection<Model> {
        return models.values
    }

    fun getSuites(): Map<String, Suite> {
        return suites
    }

    private fun getSuite(suiteId: String): Suite {
        require(suites.containsKey(suiteId)) { "Suite with id $suiteId doesn't exist" }
        return suites.getValue(suiteId)
    }

    private fun addModelIfMissing(event: Event) {
        if (models.containsKey(event.modelKey)) {
            return
        }
        val model = modelProvider.getModel(event.modelKey)
        models[model.key] = model
    }

}