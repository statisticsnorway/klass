# GitHub Copilot Instructions – Klass

## Project Context
Klass is Statistics Norway's classification/code-list system. Multi-module Maven project (Java 21), except `klass-forvaltning` which uses **Java 8** and must be built separately via `make build-klass-forvaltning`. See `AGENTS.md` for full architecture overview.

## Module Responsibilities
- **`klass-shared`**: Domain entities, repositories, services — shared library, no main app. All Flyway migrations live in `klass-api`, not here.
- **`klass-api`**: Public REST API. All new API endpoints, DTOs, and migrations go here.
- **`klass-forvaltning`**: Internal admin UI (Java 8, depends on `backport-v2` branch of `klass-shared`). Do **not** add Java 21+ features here.

## Domain Conventions
- Multi-lingual text uses `Translatable` (`no/ssb/klass/core/util/Translatable.java`) wrapping three strings: `no` (Bokmål), `nn` (Nynorsk), `en`. Use `Translatable` for any user-facing text field on entities.
- Entity hierarchy: `ClassificationFamily → ClassificationSeries → ClassificationVersion → ClassificationItem`. Always navigate through this chain — do not bypass via direct DB queries when the service layer covers the use case.
- All entities extend `BaseEntity`; audit fields (created/updated) are set via `BaseEntityInterceptor`.

## API Layer Patterns
- REST controllers live in `klass-api/src/main/java/no/ssb/klass/api/controllers/`.
- Response DTOs for HAL responses are in `klass-api/.../dto/hal/` and extend `KlassResource`.
- Use `@Transactional` at the controller level where needed (see `ClassificationController`).
- API base path: `/api/klass/v1`; admin UI served at `/klass/admin`.

## Testing
- Integration tests use REST Assured + Zonky embedded PostgreSQL. Base class: `AbstractRestApiApplicationTest`.
- Tests live in `klass-api/src/test/.../applicationtest/`. Add new endpoint tests here.
- Run a single test class: `mvn test -pl klass-api -Dtest=RestApiCodeIntegrationTest`

## Database Migrations
- Add new Flyway scripts to `klass-api/src/main/resources/db/migration/` following the naming convention `V{N}__{description}.sql`.
- Never add migrations to `klass-shared` (local test SQL only).

## Code Style
- **Always** run `mvn fmt:format` before committing — CI will reject unformatted code. Uses google-java-format AOSP style (4-space indent).
- Package prefix for all modules: `no.ssb.klass`.

## Spring Profiles for Local Dev
Typical local run combines: `api,postgres-local,hardcoded-user,mock-search,mock-mailserver,small-import`  
Forvaltning: `frontend,postgres-local,hardcoded-user,embedded-solr,mock-mailserver,small-import,skip-indexing`

