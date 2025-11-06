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

.PHONY: build-clean-klass-forvaltning
build-clean-klass-forvaltning:
	pushd klass-forvaltning && \
	${sdk} env && \
	mvn clean install; \
 	popd; \
	${sdk} env clear

.PHONY: build-klass-api
build-klass-api:
	pushd klass-api && \
	${sdk} env && \
	mvn install; \
 	popd; \
	${sdk} env clear

.PHONY: build-clean-klass-api
build-clean-klass-api:
	pushd klass-api && \
	${sdk} env && \
	mvn clean install; \
 	popd; \
	${sdk} env clear

.PHONY: run-klass-forvaltning-local-postgres
run-klass-forvaltning-local-postgres:
	pushd klass-forvaltning && \
	${sdk} env && \
	mvn spring-boot\:run  -Dspring-boot.run.profiles=frontend,postgres-local,hardcoded-user,mock-search,mock-mailserver,small-import,skip-indexing; \
	popd; \
	${sdk} env clear

.PHONY: run-klass-forvaltning-local-postgres-search
run-klass-forvaltning-local-postgres-search:
	pushd klass-forvaltning && \
	${sdk} env && \
	mvn spring-boot\:run -Dspring-boot.run.profiles=frontend,postgres-local,hardcoded-user,mock-mailserver,small-import,skip-indexing,remote-solr -Dklass.env.search.solr.url=http://localhost:8983/solr/; \
	popd; \
	${sdk} env clear


.PHONY: run-klass-api-local-postgres
run-klass-api-local-postgres:
	pushd klass-api && \
	${sdk} env && \
	mvn spring-boot\:run  -Dspring-boot.run.profiles=api,postgres-local,hardcoded-user,mock-search,mock-mailserver,small-import; \
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
	docker compose $(COMPOSE_FILE) --profile frontend down -v

.PHONY: clean-klass-forvaltning-volumes
clean-klass-forvaltning-volumes:
	docker volume rm klass_pgdata || true

.PHONY: start-klass-api-docker
start-klass-api-docker:
	docker compose $(COMPOSE_FILE) --profile api up --build -d

.PHONY: check-klass-api-docker
check-klass-api-docker:
	docker compose $(COMPOSE_FILE) --profile api ps

.PHONY: rebuild-klass-api-docker
rebuild-klass-api-docker:
	docker compose $(COMPOSE_FILE) --profile api build --no-cache klass-api

.PHONY: restart-klass-api-docker
restart-klass-api-docker:
	docker compose $(COMPOSE_FILE) --profile api restart klass-api

# Log only klass-api
.PHONY: logs-klass-api
logs-klass-api:
	docker compose $(COMPOSE_FILE) --profile api logs --tail=100 -f klass-api

.PHONY: stop-klass-api-docker
stop-klass-api-docker:
	docker compose $(COMPOSE_FILE) --profile api down -v

.PHONY: start-klass-api-open-search-docker
start-klass-api-open-search-docker:
	docker compose $(COMPOSE_FILE) --profile open-search up --build -d

.PHONY: stop-klass-api-open-search-docker
stop-klass-api-open-search-docker:
	docker compose $(COMPOSE_FILE) --profile open-search down -v

.PHONY: check-klass-api-open-search-docker
check-klass-api-open-search-docker:
	docker compose $(COMPOSE_FILE) --profile open-search ps

.PHONY: logs-klass-api-open-search
logs-klass-api-open-search:
	docker compose $(COMPOSE_FILE) --profile open-search logs --tail=100 -f klass-api-open-search