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
	pushd klass-forvaltning && mvn spring-boot\:run -Dspring-boot.run.profiles=h2-inmemory,embedded-solr,ad-offline,small-import && popd
