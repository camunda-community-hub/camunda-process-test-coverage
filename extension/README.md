## Versioning and Regression testing

The library is not including any version of Camunda but defines it as a `provided` dependency.
This allows you to use the library with any of supported versions.

The library distinguishes between the version of Camunda it is using as a version during
the compilation and the runtime version used during regression testing.

Current compile version is 7.14

Regression test versions are:

| JDK \ Camunda Version | 7.10 | 7.11 | 7.12 | 7.13 | 7.14 | 7.15 | 7.16 | 
|-----------------------|------|------|------|------|------|------|------|
| JDK 1.8 (LTS)         | yes  | yes  |  yes | yes  | yes  | yes  |  yes |
| JDK 11 (LTS)          |  no* |  no* |  no* | yes  | yes  | yes  |  yes |
| JDK 17 (LTS)          |  no* |  no* |  no* | yes  | yes  | yes  |  yes |
|                       |      |      |      |      |      |      |      |



