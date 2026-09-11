# AGENTS.md – Klass Codebase Guide

## Architecture Overview

Klass is Statistics Norway's classification/code-list system. It is a multi-module Maven project with these modules:

| Module | Java | Purpose |
|---|---|---|
| `klass-shared` | 21 | Core domain: JPA entities, repositories, services (local test SQL only) |
| `klass-api` | 21 | Public REST API (Spring Boot WAR); owns all Flyway migrations |
| `klass-mail` | 21 | Fire-and-forget email microservice (via SUP Postman) |
| `klass-index-job` | 21 | Periodic OpenSearch indexing job |
| `klass-forvaltning` | **8** | Internal admin frontend (Spring Boot WAR, separate Java version) |
| `klass-solr` | – | Solr core configuration (legacy search, standalone service) |

> **Critical**: `klass-forvaltning` uses Java 8 (see `klass-forvaltning/.sdkmanrc`). The root and all other modules use Java 21 (`/.sdkmanrc`). Building forvaltning from the root `pom.xml` is intentionally excluded — it must be built separately. `klass-forvaltning` also depends on the `backport-v2` branch of `klass-shared`, not `main`.

## Domain Model

`klass-shared/src/main/java/no/ssb/klass/core/model/` contains the core entities:
- `ClassificationFamily` → groups `ClassificationSeries` (a classification like "Standard Industrial Classification")
- `ClassificationSeries` → has multiple `ClassificationVersion` (time-bounded versions)
- `ClassificationVersion` → contains `ClassificationItem` entries (the actual codes/names)
- `CorrespondenceMap` — maps codes between two versions
- `Changelog`, `Subscriber`, `SearchWords` — supporting entities

## Build & Run

```bash
# Build all modules except forvaltning (Java 21 required)
mvn install

# Format code before opening a PR (AOSP style via google-java-format)
mvn fmt:format

# Build forvaltning (must use Java 8 via sdkman)
make build-klass-forvaltning

# Run API locally with local Postgres
make run-klass-api-local-postgres

# Run forvaltning locally with local Postgres
make run-klass-forvaltning-local-postgres
```

All Docker Compose services are defined in `klass-shared/docker-compose.yaml` and launched with profiles:

```bash
make start-klass-forvaltning-docker   # profile: frontend
make start-klass-api-docker           # profile: api
make start-klass-api-open-search-docker  # profile: open-search
make start-klass-index-job-docker     # profile: index
```

## Spring Profiles

Profiles are combined at startup. Key ones:

| Profile | Effect |
|---|---|
| `api` / `frontend` | Activates the respective application context |
| `postgres-local` | Connects to local Postgres (port 5432, user/db: `klass`) |
| `postgres-embedded` | Zonky embedded PostgreSQL (used in tests) |
| `hardcoded-user` | Bypasses authentication; role set by `klass.security.hardcoded.user.role` |
| `ad-offline` | Skips Active Directory connection |
| `small-import` | Imports a small subset of classifications (faster startup) |
| `mock-mailserver` | Logs outgoing emails instead of sending |
| `skip-indexing` | Skips search index rebuild at startup |
| `embedded-solr` | Runs Solr in-process (forvaltning dev) |
| `remote-solr` / `open-search-local` / `remote-open-search` | Search backend selection |
| `mock-search` | Mocks search entirely |
| `production` | Disables test beans, enables AD-only login |

## Database & Flyway

- PostgreSQL 17; Flyway migrations live in `klass-api/src/main/resources/db/migration/`
- Tests use Zonky embedded PostgreSQL to match the production dialect
- **Local workaround**: Migration `V4__grant_user_role.sql` fails locally unless the role exists. Create `klass-shared/initdb/init_roles.sql` containing: `CREATE ROLE "dapla-metadata-developers@groups.ssb.no";`
- Forvaltning has Flyway **disabled** (`flyway.enabled=false`); it runs only from `klass-api`

## Search Backends

Two search backends coexist:
- **Solr** (legacy): standalone service, configured via `klass-solr/`, still used by forvaltning
- **OpenSearch**: newer; `klass-index-job` handles periodic index updates; `klass-api` can use it via `open-search-local` / `remote-open-search` profiles

## Code Quality

- **Formatter**: google-java-format AOSP style (`mvn fmt:format`). CI enforces this — run it before every PR.
- **Linter**: SonarQube; install the SonarQube for IDE plugin in IntelliJ for local feedback.
- **API docs**: generated via Spring REST Docs with the `documentation` Maven profile (enabled by default during build).
- **HTTP Client tests**: IntelliJ HTTP Client files in `klass-api/doc/requests/`; requires a local `http-client.env.json` with `base_url`.

## Key Files

- `Makefile` — all common dev/build/docker commands
- `klass-shared/docker-compose.yaml` — all service definitions and profiles
- `klass-forvaltning/src/main/resources/application.properties` — full profile list with comments
- `klass-shared/src/main/resources/ehcache.xml` — Hibernate second-level cache config
- `klass-api/src/main/resources/db/migration/` — Flyway SQL migrations
- `bin/bump.sh` — version bumping for releases (invoked via `make release-{patch,minor,major}`)



