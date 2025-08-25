SHELL=/bin/zsh
MVN_AVAILABLE := $(shell command -v mvn 2> /dev/null)
SDK_AVAILABLE := $(shell [[ -s "${HOME}/.sdkman/bin/sdkman-init.sh" ]] && source "${HOME}/.sdkman/bin/sdkman-init.sh" && command -v sdk)
sdk = source "${HOME}/.sdkman/bin/sdkman-init.sh" && sdk
COMPOSE_FILE = -f klass-shared/docker-compose.yaml

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
	mvn install; \
	popd; \
	${sdk} env clear

.PHONY: build-klass-api
build-klass-api:
	pushd klass-api && \
	${sdk} env && \
	mvn install; \
 	popd; \
	${sdk} env clear

.PHONY: run-klass-forvaltning-local
run-klass-forvaltning-local:
	pushd klass-forvaltning && \
	${sdk} env && \
	mvn spring-boot\:run; \
	popd; \
	${sdk} env clear

.PHONY: run-klass-forvaltning-local-postgres
run-klass-forvaltning-local-postgres:
	pushd klass-forvaltning && \
	${sdk} env && \
	mvn spring-boot\:run  -Dspring.profiles.active=postgres-local,hardcoded-user,embedded-solr,frontend,skip-indexing,small-import,ad-offline; \
	popd; \
	${sdk} env clear

.PHONY: run-klass-forvaltning-local-postgres-search
run-klass-forvaltning-local-postgres-search:
	pushd klass-forvaltning && \
	${sdk} env && \
	mvn spring-boot\:run -Dspring.profiles.active=postgres-local,hardcoded-user,frontend,skip-indexing,small-import,ad-offline,remote-solr -Dklass.env.search.solr.url=http://localhost:8983/solr/; \
	popd; \
	${sdk} env clear


.PHONY: run-klass-forvaltning-local-mariadb
# Requires that a MariaDB instance is already running with a database called klass and a user called klass.
# The environment variable KLASS_ENV_MARIADB_PASSWORD must be specified with the password for the klass user.
run-klass-forvaltning-local-mariadb:
	pushd klass-forvaltning && \
	${sdk} env && \
	mvn spring-boot\:run -Dspring.profiles.active=mariadb,hardcoded-user,embedded-solr,frontend,skip-indexing,small-import,ad-offline; \
	popd; \
	${sdk} env clear

# The environment variable KLASS_ENV_SECURITY_IGNORED must be set to "/**" in order to skip authentication
run-klass-api-local-mariadb:
	pushd klass-api && \
	${sdk} env && \
	mvn spring-boot\:run -Dspring.profiles.active=mariadb,hardcoded-user,embedded-solr,mock-mailserver,skip-indexing,small-import,ad-offline; \
	popd; \
	${sdk} env clear

.PHONY: start-klass-forvaltning-docker
start-klass-forvaltning-docker:
	docker compose $(COMPOSE_FILE) --profile frontend up --build -d

.PHONY: logs-klass-forvaltning
logs-klass-forvaltning:
	docker compose $(COMPOSE_FILE) --profile frontend logs -f

.PHONY: stop-klass-forvaltning-docker
stop-klass-forvaltning-docker:
	docker compose $(COMPOSE_FILE) --profile frontend down

.PHONY: clean-klass-forvaltning-volumes
clean-klass-forvaltning-volumes:
	docker volume rm klass_pgdata || true

.PHONY: start-klass-api-docker
start-klass-api-docker:
	docker compose $(COMPOSE_FILE) --profile api up --build -d

.PHONY: logs-klass-api
logs-klass-api:
	docker compose $(COMPOSE_FILE) --profile api logs -f

.PHONY: stop-klass-api-docker
stop-klass-api-docker:
	docker compose $(COMPOSE_FILE) --profile api down
