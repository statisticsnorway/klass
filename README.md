[![Maintainability](https://api.codeclimate.com/v1/badges/34eed0d4c7e9abd16add/maintainability)](https://codeclimate.com/github/statisticsnorway/klass/maintainability)
[![CodeQL](https://github.com/statisticsnorway/klass/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/statisticsnorway/klass/actions/workflows/codeql-analysis.yml)

# KLASS

Spring Boot applications that handles classifications for SSB.
Klass provides a REST api that clients can use to read classifications, and a Vaadin frontend for maintaining classifications.

## Overview

Klass consists of 4 maven modules

- Klass API (Standalone application that provides the Klass API)
- Klass Forvaltning (Frontend for classification maintaining)
- Klass Shared (Classes shared between API and Forvaltning. primary database and search components)
- Klass Solr (Solr Core configuration and configuration for embedded solr for test/development)

## Build and Deploy

Building the project will output war files for **Klass API** & **Klass Forvaltning** and a zip file (WiP) for **Klass Solr**.

You can find these in each maven modules target folder.

```
klass-api/target/klass-api-{Version}.war
klass-forvaltning/target/klass-forvaltning-{Version}.war
klass-solr/klass-solr-{Version}.zip (WiP)
```

## Database

Klass is configured to use Flyway for database initialising and migration.
You can find the collection of SQL scripts in the Klass-shared module under `src/main/resources/db/migration`

If the classification tables are empty Klass will by default attempt to import data from its predecessor.
This process can take quite some time as there is a lot of data and its also sent to Solr to populate the search index.

Tips: If you are only setting up Klass for testing/development purposes you can use the `small-import` spring profile to reduce the amount of data being imported.

## Development

### Requirements

- Maven: <https://maven.apache.org/install.html>
- Sdkman: <https://sdkman.io/install>
- Java 17: `sdk install java 17.0.15-tem`
- Java 8 (for Klass Forvaltning): `sdk install java 8.0.452-tem`

### Code Quality configuration

We follow a multi-layered approach based on the "shift-left" philosophy where problems are addressed as early as possible, ideally while writing the code in the IDE. Checks are performed at three stages:

- In the IDE through extensions
- In CI/CD as a hard check on PRs

Developers are encouraged to install the IDE plugins/extensions to avoid pain at the PR stage.

#### Plugins/extensions

##### IntelliJ

- https://plugins.jetbrains.com/plugin/7973-sonarqube-for-ide
- https://plugins.jetbrains.com/plugin/8527-google-java-format

#### Lint

We use Sonarqube for linting. This runs a range of checks on code quality. It runs in CI/CD and it's a good idea to install the extension in your IDE to get feedback as you code.

#### Formatting

We use google-java-format for code formatting. This avoids unproductive discussions about minutiae of bracket placement etc. Make sure it runs in your IDE and you install the pre-commit hook

### Configuration

#### GitHub Packages

In order to download dependencies from GitHub Packages we must authenticate Maven. See the documentation here: <https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-with-a-personal-access-token>

This may be done by generating a Personal Access Token (classic) on GitHub with the `write:packages` scope. Remember to configure SSO. The following configuration may then be placed in your `~/.m2/settings.xml` file.

```xml

<servers>
    <server>
        <id>github</id>
        <username>USERNAME</username>
        <password>TOKEN</password>
    </server>
</servers>
```

#### Docker

##### Colima on Mac

Colima should have at least 5G memory and the project folder must be mounted. Change this in `~/.colima/default/colima.yaml` e.g.

```yaml
memory: 5
...
mounts:
  - location: /Users/perolsen/Repository/github/klass
    writable: false
```

### Introduction

It's recommended to build with maven before starting development as some classes are generated as part of the build process.

Each app has an `.sdkmanrc` file which may be used to configure the Java version to use. This may be activated by entering the directory and running the `sdk env` command. A `Makefile` is also provided with relevant commands for building each app. See <https://sdkman.io/usage#env-command> for more details

### Klass API

#### Build

Build the app: `make build-klass-api`

#### Docker compose

TODO

### Klass Forvaltning

#### Build

`make build-klass-forvaltning`

#### Run

`make run-klass-forvaltning-local`

Visit <http://127.0.0.1:8081/klassui>

### Spring profiles

Klass API and Klass Forvaltning utilize Spring boot and heavily rely on Spring Profiles to make development and debugging easier.
below is a quick summary of the profiles available (see _application.properties_ for more details)

```
# Application profiles:
#----------------------
# Profiles for production
#   production          = no test beans and only Active Directory login
#
# Profiles for development
#   ad-offline          = will only use test/embeded AD (apacheDS) [Forvaltning only]
#   small-import        = imports a small number of classifications from legacy system, useful during development
#   mock-mailserver     = outgoing emails are only logged
```

### Build profiles

The profile named `documentation` will generate API documentation with AsciiDoc (default: enabled)

### Run / Debug

You can start an application using maven with the following command

```shell
mvn spring-boot:run
```

Start the forvaltning app with

```shell
make run-klass-forvaltning-local
```

Or from your IDE using the `KlassApiApplication` / `KlassForvaltningApplication` classes.
IntelliJ is recommended, it makes it very easy to start spring boot applications and will make sure your run/debug configuration has all the necessary dependencies (will include maven dependencies with provided scope).

Frontend may be accessed at:

<http://localhost:8081/klassui>

REST api documentation may be accessed at

<http://localhost:8081/api/klass/v1/api-guide.html>

[![IntelliJ](docs/troubleshoot_workdir_small.png)](./docs/troubleshoot_workdir.png)
