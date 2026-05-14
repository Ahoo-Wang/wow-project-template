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

# Run with coverage verification (80% minimum)
./gradlew :domain:jacocoTestCoverageVerification

# Static analysis (detekt)
./gradlew detekt

# Generate API docs
./gradlew dokkaHtml

# Build server distribution
./gradlew server:installDist

# Clean build
./gradlew clean build
```

## Module Structure & Dependency Flow

```
dependencies (BOM/platform) → api → domain → server
                                        ↓
                                     client (TypeScript, auto-generated)
```

- **`dependencies`** - Centralized BOM/dependency management via Gradle platform. All version catalog entries in `gradle/libs.versions.toml`.
- **`api`** - Public API contracts: commands, events, state interfaces, and client interfaces (publishable library). Uses KSP (`wow-compiler`).
- **`domain`** - Aggregate roots, state classes, and sagas (publishable library). Depends on `api`. Uses KSP (`wow-compiler`). 80% test coverage required.
- **`server`** - Spring Boot WebFlux application, projectors, controllers (not published). Uses both KSP and kapt.
- **`bom`** - Bill of Materials for consumers.
- **`code-coverage-report`** - Aggregated coverage reports.
- **`client`** - Auto-generated TypeScript client library via `fetcher-generator`. Build with `pnpm generate && pnpm build`.

## Architecture (Wow Framework Patterns)

### Service Definition

Each bounded context is defined by a service object that declares constants and aggregate scopes:

```kotlin
@BoundedContext(name = SERVICE_NAME, alias = SERVICE_ALIAS,
    aggregates = [BoundedContext.Aggregate(aggregateName, packageScopes = [CommandClass::class])])
object DemoService {
    const val SERVICE_NAME = "demo-service"
    const val SERVICE_ALIAS = "demo"
    const val DEMO_AGGREGATE_NAME = "demo"
}
```

### API Module Conventions

- **Contract interfaces** define shared fields: `IDemoInfo` (data), `IDemoState` extends `Identifier` + `IDemoInfo`
- **Commands** are data classes with `@CommandRoute`, `@CreateAggregate` annotations, implementing contract interfaces. Use Jakarta validation (`@NotBlank`).
- **Events** are data classes with `@Event` annotation. Naming: past tense of the command (e.g., `CreateDemo` → `DemoCreated`).
- **Service interfaces** use Spring `@HttpExchange` for type-safe HTTP client contracts.

### Aggregate Root (`domain` module)

- Aggregate class: `@AggregateRoot` annotation, takes state as constructor parameter
- Command handlers: `@OnCommand` methods that return events
- State class: `@OnSourcing` methods that apply events to state. Mutable properties with `private set`.

### Saga (`domain` module)

- `@StatelessSaga` annotation on the class
- `@OnEvent` methods receive events and return `CommandBuilder` to send follow-up commands

### Server Module

- **Controllers** implement the API service interface (e.g., `DemoController : DemoApi`) and are annotated with `@RestController`. Wow auto-generates command/query routes from the interface.
- **Projectors**: `@ProjectionProcessor` for read model updates from events
- **Inter-service clients**: `@CoApi(serviceId)` interfaces extending the API service interface (e.g., `DemoClient : DemoApi`)
- Main class: `ServerKt` in `me.ahoo.wow.template.server`, uses `@BoundedContext` and `@SpringBootApplication` with `scanBasePackageClasses`

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

`fork` creates a branching state to test subsequent commands on the same aggregate.

### Code Generation (KSP)

KSP with `wow-compiler` runs in both `api` and `domain` modules, generating:
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

## Key Dependencies

- **Wow Framework**: `me.ahoo.wow:wow-*` (api, spring, webflux, test, compiler)
- **CoApi**: `me.ahoo.coapi:coapi-*` (type-safe HTTP client auto-configuration)
- **CoSec**: `me.ahoo.cosec:cosec-*` (authorization, optional)
- **CoCache**: `me.ahoo.cocache:cocache-*` (distributed caching, optional)
- **CoSiD**: `me.ahoo.cosid:cosid-*` (distributed ID generation)
- **Spring Boot**: WebFlux-based reactive stack, Spring Boot 4.x
- **Kotlin**: JVM 17 target with `-Xjsr305=strict`, `-Xjvm-default=all-compatibility`

## Configuration Files

- Detekt config: `config/detekt/detekt.yml`
- Logback: `config/logback.xml`
- Spring: `server/src/main/resources/application.yaml`
- Version catalog: `gradle/libs.versions.toml`
