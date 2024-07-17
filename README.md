# OSGIExamples-GenericObjects

## Scope

This repository demonstrates the implementation of OSGi-based generic objects for ECTR by using the example of a Jira integration.

The implementation consists of three parts:

1. Integrate Jira issues as first-level objects in ECTR that can be added to ECTR folders and display the issue ID as URL to jump to the Jira issue using the web browser.
1. Add a search provider for Jira issues that integrates with the ECTR Quick Search (shortcut `Ctrl-F`).
1. Add a Smart Container "Related Jira issues" under Material Master ECTR objects that lists all Jira issues where the issue description/text contains the material number.

## Development setup

1. Adjust the [pom.xml](pom.xml): Maintain `ectr.install.dir` property such that it points to the ECTR installation directory.

1. Adjust the [default.txt](src/main/resources/customize/config/default.txt):

    1. In your Jira instance, create a __personal access token__, see [Atlassian Support: Using Personal Access Tokens](https://confluence.atlassian.com/enterprise/using-personal-access-tokens-1026032365.html).
    1. Use your personal access token as the value for preference `org.dogoodthings.ectr.genericObjects.jira.token`.
    1. Maintain preference `org.dogoodthings.ectr.genericObjects.jira.baseUrl`, e.g. `https://jira.mycompany.com`.

         __Note__: __Omit__ a trailing slash in the URL.

1. Build the project:

    ```shell
    mvn clean compile package
    ```

   This will automatically create an addon folder `jira-integration` in the ECTR installation directory under `addons`. If the build succeeds, the addon is ready to be used (ECTR needs to be restarted).
