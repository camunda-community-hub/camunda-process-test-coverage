# How to Contribute

We'd love you to contribute to this project by filing bugs, helping others on the [issue tracker](https://github.com/camunda/camunda-process-test-coverage/issues), and by contributing features/bug fixes through pull requests.

Read more on [how to get the project up and running](#setting-up-the-project-locally).


## Creating Issues

Please use our [issue tracker](https://github.com/camunda/camunda-process-test-coverage/issues) for project communication.
When using the issue tracker:

* Be descriptive when creating an issue (what, where, when and how does a problem pop up)?
* Attach steps to reproduce (if applicable)
* Attach code samples, configuration options or stack traces that may indicate a problem
* Be helpful and respect others when commenting

Create a pull request if you would like to have an in-depth discussion about some piece of code.


## Setting Up the Project Locally

The project is build using [Maven 3](https://maven.apache.org/) as build tool.  
To build the project by yourself, go to your cmd line and enter ```mvn clean install``` on the root of the checked out project.  

1. git clone https://github.com/camunda/camunda-process-test-coverage.git
2. cd camunda-process-test-coverage/
3. mvn clean install

## Getting Comfortable
* [Implementation Notes](IMPLEMENTATION.md)

## Creating Pull Requests

We use pull requests for feature discussion and bug fixes. 
If you are not yet familiar on how to create a pull request, [read this great guide](https://gun.io/blog/how-to-github-fork-branch-and-pull-request).

Some things that make it easier for us to accept your pull requests:

* Changed code is not reformatted
* Use four tabs instead of spaces for whitespace
* The code is tested. Add test cases for the problem you are solving.
* The `mvn clean install` build passes including tests
* The work is combined into a single commit

We'd be glad to assist you if you do not get these things right in the first place.

