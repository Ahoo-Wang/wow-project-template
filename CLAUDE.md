# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **Wow framework** microservice template implementing DDD (Domain-Driven Design) + Event Sourcing + CQRS patterns. The Wow framework handles event sourcing, command processing, and projection automatically through annotations.

## Skills Reference

Invoke these skills when working on relevant tasks:

- `/wow` — Wow framework patterns: aggregate modeling, command gateway, saga, projection, query DSL, testing DSL
- `/fluent-assert` — Kotlin fluent assertions: use `value.assert()` instead of AssertJ's `assertThat(value)`

## Build & Test Commands

```bash
# Build all modules
./gradlew build

# Run tests
./gradlew test

# Run a single test class
./gradlew :domain:test --tests "me.ahoo.wow.template.domain.demo.DemoSpec"

# Run a single test method
./gradlew :domain:test --tests "me.ahoo.wow.template.domain.demo.DemoSpec.*onCreate*"

# Coverage verification (domain ≥ 80%, Kover; also runs as part of `check`)
./gradlew :domain:koverVerify

# Static analysis (detekt)
./gradlew detekt

# Generate API docs (Dokka v2)
./gradlew dokkaGenerate

# Build server distribution
./gradlew server:installDist

# Run server locally (dev profile: debug logging, all actuator endpoints)
./gradlew server:run --args='--spring.profiles.active=dev'

# Clean build
./gradlew clean build
```

## Module Structure & Dependency Flow

```
dependencies (BOM/platform) → api → domain → server
                               ↓                ↓
                           api-client        client (TypeScript, generated from server OpenAPI)
```

- **`build-logic`** - Included build with convention plugins (`wow.base/kotlin/library/platform/publish/wow-compiler/application/coverage-report-conventions`). All shared build configuration lives here; module scripts only declare plugins and dependencies.
- **`dependencies`** - Centralized BOM/dependency management via Gradle platform. All version catalog entries in `gradle/libs.versions.toml`.
- **`api`** - Published language: commands, events, state contracts, query view models (publishable library). Depends only on annotation/contract artifacts (`wow-api`, validation, jackson/swagger annotations). Uses KSP (`wow-compiler`).
- **`api-client`** - Optional typed CoApi client for other services (`DemoClient : ReactiveSnapshotQueryApi<DemoView>`). Kept separate so `api` consumers don't inherit HTTP client infrastructure.
- **`domain`** - Aggregate roots, state classes, and sagas (publishable library). Depends on `api` + `wow-core` only (no Spring). Uses KSP (`wow-compiler`). 80% test coverage required.
- **`server`** - Spring Boot WebFlux host: projectors, wiring, configuration (not published). No KSP/kapt needed.
- **`bom`** - Bill of Materials for consumers.
- **`code-coverage-report`** - Aggregated Kover coverage report (uploaded to Codecov).
- **`client`** - Generated TypeScript client via `fetcher-generator`. Regenerate with the server running: `pnpm generate && pnpm lint && pnpm build`. CI fails if `client/src/generated` drifts from the server contract.

## Architecture (Wow Framework Patterns)

### Service Definition

Each bounded context is defined by a service object (in `api`) that declares constants and aggregate scopes. Register every new aggregate here:

```kotlin
@BoundedContext(name = SERVICE_NAME, alias = SERVICE_ALIAS,
    aggregates = [
        BoundedContext.Aggregate(DEMO_AGGREGATE_NAME, packageScopes = [CreateDemo::class]),
        BoundedContext.Aggregate(REVIEW_AGGREGATE_NAME, packageScopes = [CreateReview::class]),
    ])
object DemoService {
    const val SERVICE_NAME = "demo-service" // must equal spring.application.name
    const val SERVICE_ALIAS = "demo"
    const val DEMO_AGGREGATE_NAME = "demo"
    const val REVIEW_AGGREGATE_NAME = "review"
}
```

`domain` also needs `@BoundedContext(name = SERVICE_NAME)` (`DemoBoundedContext`): its KSP fails without it. `server` does not need one — the `api` scope (`me.ahoo.wow.template`) already covers it.

### API Module Conventions

- **Contract interfaces** define shared input/state shapes: `IDemoInfo` (data), `IDemoState` extends `Identifier` + `IDemoInfo` + `status`. Commands and state may implement them.
- **Commands** are data classes with `@CommandRoute` (and `@CreateAggregate` for creation). Validate with `@field:NotBlank` etc. A command and its resulting event live in the same file.
- **Internal commands** (only sent by sagas, e.g. `PublishDemo`, `CreateReview`) use `@CommandRoute(enabled = false)` so they cannot be invoked over HTTP — neither via their own route nor via `/wow/command/send`.
- **Events** are self-contained data classes with `@Event`, named in past tense (`CreateDemo` → `DemoCreated`). **Events must not implement shared contract interfaces**: persisted events are immutable and their schema must evolve independently.
- **Query view models** (e.g. `DemoView`) are concrete data classes used to deserialize snapshot query results.

### Aggregate Root (`domain` module)

- Aggregate class: `@AggregateRoot` annotation, takes state as constructor parameter
- Command handlers: `@OnCommand` methods that **check invariants against current state** (`check`/`require` → `IllegalState`/`IllegalArgument` error, HTTP 400) and return events. Handlers never mutate state.
- State class: `@OnSourcing` methods that apply events to state. Mutable properties with `private set`.
- Delete/recover use Wow's built-in `DefaultDeleteAggregate` / `DefaultRecoverAggregate`; no custom code needed.

### Saga (`domain` module)

- `@StatelessSaga` annotation on the class
- `@OnEvent` methods receive events and return `CommandBuilder` to send follow-up commands to **other** aggregates (cross-aggregate coordination). Do not use a saga to rewrite the state of the aggregate that just emitted the event.
- Events are delivered at least once: make follow-up commands idempotent, e.g. `DemoReviewSaga` reuses the demo id as the review id so a duplicate `CreateReview` is rejected.
- Example flow (`DemoReviewSaga`): `DemoCreated → CreateReview`, `ReviewApproved → PublishDemo`, `ReviewRejected → RejectDemo`.

### Server Module

- Wow auto-generates command routes (from `@CommandRoute`), snapshot/event query routes and `GET /tenant/{tenantId}/{aggregate}/{id}/state` — no controllers needed. Add a `@RestController` only for custom endpoints.
- **Projectors**: `@ProjectionProcessor` with `@OnEvent` methods for read model updates from events
- **Inter-service clients**: `@CoApi(serviceId)` interfaces in `api-client` (e.g., `DemoClient : ReactiveSnapshotQueryApi<DemoView>`); snapshot queries need a query backend (Mongo/Elasticsearch), not available in-memory.
- Main class: `ServerKt` in `me.ahoo.wow.template.server`, `@SpringBootApplication(scanBasePackageClasses = [DemoBoundedContext::class, Server::class])`
- End-to-end tests (`DemoReviewFlowTest`) drive HTTP commands with `WebTestClient` and poll aggregate state, since saga-driven commands are asynchronous.

### Testing (Given-When-Expect)

```kotlin
class DemoSpec : AggregateSpec<Demo, DemoState>({
    on {
        val command = CreateDemo(data = "test")
        whenCommand(command) {
            expectNoError()
            expectEventType(DemoCreated::class)
            expectState {
                data.assert().isEqualTo(command.data)
            }
            fork {
                whenCommand(UpdateDemo(data = "updated")) {
                    expectEventType(DemoUpdated::class)
                }
            }
        }
    }
})
```

**Key testing imports:**
- `me.ahoo.test.asserts.assert` - FluentAssert (use `.assert()` not AssertJ's `assertThat`)
- `me.ahoo.wow.test.AggregateSpec` - Aggregate test base
- `me.ahoo.wow.test.SagaSpec` - Saga test base

`fork("name") { ... }` creates a branching state to test subsequent commands on the same aggregate. Assert domain errors with `expectError<IllegalStateException> { message.assert().contains(...) }`.

### Code Generation (KSP)

KSP with `wow-compiler` runs in both `api` and `domain` modules (via `wow.wow-compiler-conventions`), generating:
- `AggregatesMetadata.kt` - Aggregate metadata registration
- `*Properties.kt` - State property accessors

Run `./gradlew build` to regenerate after annotation changes.

## Default Configuration

The default `application.yaml` uses **in-memory** implementations for all infrastructure:
- Command bus: `in_memory`
- Event bus: `in_memory`
- Event sourcing store: `in_memory`
- Snapshot storage: `in_memory`

To use production backends, uncomment the relevant dependencies in `server/build.gradle.kts` (Mongo, Kafka, Redis, Elasticsearch) and update `application.yaml`.

Configuration layering: `server/src/main/resources/application.yaml` (in the jar) is the baseline; the distribution's `config/application.yaml` (`server/src/dist/config`) overrides it via `spring.config.additional-location`. Defaults are production-safe (actuator exposes only `health`/`info`, info-level logging); the `dev` profile enables debug logging and all actuator endpoints.

## Key Dependencies

- **Wow Framework**: `me.ahoo.wow:wow-*` (api, spring, webflux, test, compiler)
- **CoApi**: `me.ahoo.coapi:coapi-*` (type-safe HTTP client auto-configuration)
- **CoSec**: `me.ahoo.cosec:cosec-*` (authorization, optional)
- **CoCache**: `me.ahoo.cocache:cocache-*` (distributed caching, optional)
- **CoSiD**: `me.ahoo.cosid:cosid-*` (distributed ID generation)
- **Spring Boot**: WebFlux-based reactive stack, Spring Boot 4.x
- **Kotlin**: JVM 17 toolchain with `-Xjsr305=strict`, `jvmDefault = ENABLE` (configured in `wow.kotlin-conventions`)

## Configuration Files

- Detekt config: `config/detekt/detekt.yml`
- Logback: `config/logback.xml`
- Spring: `server/src/main/resources/application.yaml`
- Version catalog: `gradle/libs.versions.toml` (also used by `build-logic` for Gradle plugin versions)
- Build conventions: `build-logic/src/main/kotlin/wow.*-conventions.gradle.kts`
