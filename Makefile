SHELL=/bin/zsh
MVN_AVAILABLE := $(shell command -v mvn 2> /dev/null)
SDK_AVAILABLE := $(shell [[ -s "${HOME}/.sdkman/bin/sdkman-init.sh" ]] && source "${HOME}/.sdkman/bin/sdkman-init.sh" && command -v sdk)
sdk = source "${HOME}/.sdkman/bin/sdkman-init.sh" && sdk

.PHONY: check
check:
ifndef MVN_AVAILABLE
	$(error "Maven is not available. Please install it before continuing.")
endif
ifndef SDK_AVAILABLE
	$(error "Sdkman is not available. Please install it before continuing.")
endif
	@echo "Tools installed and ready"


.PHONY: default
default: | help

.PHONY: help
help:
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

.PHONY: build-klass-forvaltning
build-klass-forvaltning:
	pushd klass-forvaltning && \
	${sdk} env && \
	mvn -B install -P nexus; \
	popd; \
	${sdk} env clear

.PHONY: build-klass-api
build-klass-api:
	pushd klass-api && \
	mvn -B install -P nexus; \
 	popd

.PHONY: run-klass-forvaltning-local
run-klass-forvaltning-local:
	pushd klass-forvaltning && \
	${sdk} env && \
	mvn spring-boot\:run --settings=../.maven.settings.xml -P nexus; \
	popd; \
	${sdk} env clear

.PHONY: run-klass-forvaltning-local-mariadb
# Requires that a MariaDB instance is already running with a database called klass and a user called klass.
# The environment variable KLASS_ENV_MARIADB_PASSWORD must be specified with the password for the klass user.
run-klass-forvaltning-local-mariadb:
	pushd klass-forvaltning && \
	mvn spring-boot\:run -Dspring.profiles.active=mariadb,embedded-solr,frontend,skip-indexing,small-import,ad-offline -P nexus; \
	popd

# The environment variable KLASS_ENV_SECURITY_IGNORED must be set to "/**" in order to skip authentication
run-klass-api-local-mariadb:
	pushd klass-api && \
	mvn spring-boot\:run -Dspring.profiles.active=mariadb,embedded-solr,mock-mailserver,skip-indexing,small-import,ad-offline -P nexus; \
	popd
