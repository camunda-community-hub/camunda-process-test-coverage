package org.camunda.community.process_test_coverage.sonar

import org.sonar.api.resources.AbstractLanguage

class BpmnLanguage : AbstractLanguage(KEY, NAME) {

    companion object {
        const val NAME = "BPMN"
        const val KEY = "bpmn"
    }

    override fun getFileSuffixes() = arrayOf(".bpmn")

}