## Setup reporting directory

Optionally, you might configure output path for reports. By default the reports are written to `./target/process-test-coverage/`. To change it you can set the system property `camunda-process-test-coverage.target-dir-root`.

### Maven
```xml
<plugin>
<groupId>org.apache.maven.plugins</groupId>
<artifactId>maven-surefire-plugin</artifactId>
<version>2.22.2</version>
<configuration>
  <systemPropertyVariables>
    <camunda-process-test-coverage.target-dir-root>${project.build.directory}/my-coverage-reports/</camunda-process-test-coverage.target-dir-root>
  </systemPropertyVariables>
</configuration>
</plugin>
```

### Gradle (KTS)
```kotlin
tasks {
    withType<Test> {
        systemProperties = mapOf(
            "camunda-process-test-coverage.target-dir-root" to "$buildDir/my-coverage-reports/"
        )
    }
}
```
