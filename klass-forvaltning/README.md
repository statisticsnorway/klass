# Klass forvaltning

`klass-forvaltning` is Klass's internal administration interface. It is a
separate Spring Boot WAR application and is not built together with the other
Klass modules.

## Important differences

- It is built with **Java 8**, not Java 21. The required SDK is specified in
  [`.sdkmanrc`](.sdkmanrc).
- It uses the older Klass shared libraries and the Solr-based search setup.
- Flyway migrations are run by `klass-api`; they are not run by
  `klass-forvaltning`.
- Build and release commands must be run from this directory (or after changing
  into it), rather than from the root Maven project.

## Build

With SDKMAN installed, SDKMAN can select the Java version automatically when
entering this directory. Build the application with:

```bash
mvn clean install
```

The resulting WAR is written to `target/` and can be run in the same way as a
Spring Boot application, or packaged into the provided Docker image.

## Release and deployment

Releases are performed with the Maven Release Plugin. Before starting, make
sure the working tree is clean, you are on the branch to release, and Maven is
authenticated for the configured GitHub Packages repository.

Run the two release steps from `klass-forvaltning`:

```bash
mvn release:prepare
mvn release:perform
```

`release:prepare` updates the Maven version, commits the release changes, and
creates a tag named `forvaltning-v<version>`. `release:perform` checks out that
tag, builds the release, and deploys the artifact using the repository and
distribution settings in `pom.xml`.

The forvaltning release is independent of releases of `klass-api` and the
other Java 21 modules. Do not use the root project's release process to deploy
this application.


