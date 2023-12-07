If you are interested in developing and building the project please read the following the instructions carefully.

## Version control

To get sources of the project, please execute:

```sh
git clone https://github.com/camunda-community-hub/camunda-process-test-coverage.git
cd camunda-process-test-coverage
```

We are using gitflow in our git SCM for naming branches. That means that you should start from `develop` branch,
create a `feature/<name>` out of it and once it is completed create a pull request containing
it. Please squash your commits before submitting and use semantic commit messages, if possible.

## Project Build

Perform the following steps to get a development setup up and running.

```sh
./mvnw clean install
```

## Regression Tests

By default, the build command will ignore the run of the regression tests. In order to run the regression tests, please
call from your command line:

```sh
./mvnw -Pregression-test
```

## Project build modes and profiles

### Camunda Version

The library is compiled against an older version of Camunda (see camunda.compile.version property in parent POM).
To select a Camunda version for the tests you can start with a designated profile e.g. camunda-bpm-engine-7.18.

### Documentation

We are using MkDocs for generation of a static site documentation and rely on markdown as much as possible.

!!! note

    If you want to develop your docs in 'live' mode, run `mkdocs serve` and access
    the http://localhost:8000/ from your browser.

For creation of documentation, please run:

#### Generation of JavaDoc and Sources

By default, the sources and javadoc API documentation are not generated from the source code. To enable this:

```sh
./mvnw clean install -Prelease -Dgpg.skip=true
```

### Continuous Integration

Github Actions are building all branches on commit hook (for codecov).
In addition, a Github Actions are used to build PRs and all branches.

### Release Management

The release is produced by using the github feature to "Publish a Release".

#### What modules get deployed to repository

Every Maven module is enabled by default. If you want to change this, please provide the property

```xml
<maven.deploy.skip>true</maven.deploy.skip>
```

inside the corresponding `pom.xml`. Currently, all `examples` are _EXCLUDED_ from publication into Maven Central.`
`