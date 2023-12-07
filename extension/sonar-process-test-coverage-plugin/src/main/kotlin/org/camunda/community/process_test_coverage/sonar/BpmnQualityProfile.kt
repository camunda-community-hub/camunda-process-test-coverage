package org.camunda.community.process_test_coverage.sonar

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition


class BpmnQualityProfile : BuiltInQualityProfilesDefinition {
    override fun define(context: BuiltInQualityProfilesDefinition.Context) {
        val profile = context.createBuiltInQualityProfile("BPMN Rules", BpmnLanguage.KEY)
        profile.setDefault(true)
        profile.done()
    }
}