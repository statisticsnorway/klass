.PHONY: default
default: | help

.PHONY: help
help:
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

.PHONY: build-all
build-all:
	mvn -B clean install --settings=.maven.settings.xml --file pom.xml -DskipTests -P nexus

.PHONY: run-klass-forvaltning-local
run-klass-forvaltning-local:
	pushd klass-forvaltning && mvn spring-boot\:run -P nexus && popd

.PHONY: run-klass-forvaltning-local-mariadb
# Requires that a MariaDB instance is already running with a database called klass and a user called klass.
# The environment variable KLASS_ENV_MARIADB_PASSWORD must be specified with the password for the klass user.
run-klass-forvaltning-local-mariadb:
	pushd klass-forvaltning && \
	mvn spring-boot\:run -Dspring.profiles.active=mariadb,embedded-solr,frontend,skip-indexing,small-import,ad-offline -P nexus && \
	popd
