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

# Clean build
./gradlew clean build
```

## Module Structure

- **`api`** - Public API contracts, commands, events, and state interfaces (publishable library)
- **`domain`** - Aggregate roots, state classes, and sagas (publishable library)
- **`server`** - Spring Boot application, projectors, controllers (not published)
- **`dependencies`** - BOM/dependency management (JavaPlatform)
- **`bom`** - Bill of Materials for consumers
- **`code-coverage-report`** - Aggregated coverage reports

## Architecture (Wow Framework Patterns)

### Aggregate Root (`domain` module)
- Aggregate class: `@AggregateRoot` annotation, takes state as constructor parameter
- Command handlers: `@OnCommand` methods that return events
- State class: `@OnSourcing` methods that apply events to state

### API Module
- Commands: data classes implementing Wow command interfaces
- Events: data classes for domain events
- State interfaces: `IDemoState` for read models
- Service interfaces: `DemoApi` for client contracts

### Server Module
- Projectors: `@ProjectionProcessor` for read model updates
- Controllers: Standard Spring WebFlux controllers
- Main class: `ServerKt` in `me.ahoo.wow.template.server`

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

## Key Dependencies

- **Wow Framework**: `me.ahoo.wow:wow-*` (api, spring, webflux, test, compiler)
- **KSP**: Annotation processing via `me.ahoo.wow:wow-compiler`
- **Spring Boot**: WebFlux-based reactive stack
- **Kotlin**: JVM 17 target with `-Xjsr305=strict`

## Code Generation

KSP generates:
- `AggregatesMetadata.kt` - Aggregate metadata registration
- `*Properties.kt` - State property accessors

Run `./gradlew build` to regenerate after annotation changes.

## Configuration

- Detekt config: `config/detekt/detekt.yml`
- Logback: `config/logback.xml`
- Spring: `server/src/main/resources/application.yaml`
