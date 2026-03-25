# AGENTS.md - Agentic Coding Guidelines

This document provides guidelines for AI agents working in this codebase.

## Project Overview

This is a DDD project template built with:
- **Backend**: Kotlin/JVM with Spring Boot (Wow framework)
- **Frontend**: TypeScript client library with Vite
- **Build**: Gradle (Kotlin DSL) for backend, Vite for client
- **Testing**: JUnit Jupiter (Kotlin), Vitest (TypeScript)

## Build Commands

### Kotlin/Gradle (Backend)
```bash
# Full build & Run all tests
./gradlew build
./gradlew test

# Run tests for specific module
./gradlew domain:test
./gradlew api:test
./gradlew server:test

# Run a single test class
./gradlew test --tests "me.ahoo.wow.template.domain.demo.DemoSagaSpec"
# Run specific test method
./gradlew test --tests "me.ahoo.wow.template.domain.demo.DemoSagaSpec.contextLoads"

# Check code style (Detekt)
./gradlew detekt
./gradlew detektAutoFix  # auto-fix

# Build without tests
./gradlew build -x test

# Clean and rebuild
./gradlew clean build

# Generate coverage report
./gradlew domain:jacocoTestReport
# View at: domain/build/reports/jacoco/test/html/index.html

# Verify coverage
./gradlew domain:jacocoTestCoverageVerification

# Run server
./gradlew server:run
./gradlew server:installDist
```

### TypeScript/Client
```bash
cd client
pnpm install
pnpm build
pnpm test                    # with coverage
pnpm test -- --watch        # watch mode
pnpm test -- src/xxx.test.ts # single file
pnpm lint --fix             # lint with auto-fix
pnpm format                 # format code
pnpm clean                  # clean dist & generated
```

## Kotlin Code Style

**Formatting**: Line length max 300, no trailing commas, 4 spaces indent

**Naming**: Classes PascalCase, functions camelCase, constants SCREAMING_SNAKE_CASE

**Imports**:
- Explicit imports for project code
- Wildcard allowed: `java.util.*`, `org.assertj.core.api.Assertions.*`

**Annotations**:
- `@Suppress("unused")` for intentionally unused members
- `@Summary` for Chinese documentation of commands/events
- Prefer data classes for DTOs and events

**Testing**:
- Extend `SagaSpec` for saga tests
- Use `me.ahoo.test.asserts.assert` for assertions (FluentAssert library)
- Test naming: `ClassNameSpec`

## FluentAssert (Kotlin Assertions)

This project uses [FluentAssert](https://github.com/Ahoo-Wang/FluentAssert) for Kotlin test assertions.

### Quick Start
```kotlin
import me.ahoo.test.asserts.assert

// Instead of: assertThat(value).isEqualTo(expected)
// Use: value.assert().isEqualTo(expected)
42.assert().isGreaterThan(0)
"hello".assert().startsWith("hel")
listOf(1, 2, 3).assert().hasSize(3).contains(2)
```

### Supported Types
- **Primitives**: `Boolean`, `Int`, `Long`, `Double`, `BigDecimal`, etc.
- **Strings**: `startsWith`, `endsWith`, `contains`, `hasLength`, `matches`
- **Collections**: `hasSize`, `contains`, `doesNotContain`, `allMatch`, `element`
- **Maps**: `containsKey`, `containsValue`, `containsEntry`
- **Optional**: `isPresent`, `isEmpty`, `contains`
- **Time**: `Instant`, `LocalDate`, `LocalDateTime`, `Duration`, etc.
- **Exceptions**: `assertThrownBy<ExceptionType> { code }.assert().hasMessage(...)`
- **I/O**: `Path`, `File`, `URL`, `URI`

### Saga Test Example
```kotlin
import me.ahoo.test.asserts.assert
import me.ahoo.wow.test.SagaSpec

class DemoSagaSpec : SagaSpec<DemoSaga>({
    on {
        val demoCreated = DemoCreated("data")
        whenEvent(demoCreated) {
            expectNoError()
            expectCommandBody<UpdateDemo> {
                data.assert().isEqualTo("updated")
            }
        }
    }
})
```

### Key Points
- Import: `import me.ahoo.test.asserts.assert`
- Null-safe: all extension functions accept nullable types
- Chaining: assertions return AssertJ objects, chain naturally

## TypeScript Code Style

**Types**: Strict TypeScript (`strict: true`), avoid `any`, use interfaces

**Imports**:
- `import { type X }` for type-only imports
- Relative imports for internal code
- Generated types in `src/generated/` - do not edit manually

**Naming**: Interfaces PascalCase, functions/variables camelCase

**Error Handling**: Use typed error classes, prefer Result pattern over exceptions

**Testing**: Use Vitest with `describe`/`it` patterns

## Project Structure

```
├── api/          # API layer - Commands, Events, View Models
├── domain/       # Domain layer - Aggregates, Sagas, Business rules
│   └── src/test/kotlin/    # Domain tests
├── server/       # Application entry point, Spring Boot
├── client/       # TypeScript client library
│   └── src/generated/      # Auto-generated - do not edit
├── config/detekt/ # Detekt rules
└── build.gradle.kts
```

## Module Dependencies

```
server → domain → api
         ↓
      client (generated from api)
```

- `api`: Public interfaces, commands, events (no implementation)
- `domain`: Business logic, aggregates, sagas
- `server`: Spring Boot application, wiring
- `client`: TypeScript types and clients generated from API

## Key Conventions

1. **DDD Patterns**: Commands create events, aggregates handle commands, sagas coordinate multi-step workflows
2. **Generated Code**: Client code in `client/src/generated/` is auto-generated - do not edit manually
3. **Test Coverage**: Domain module requires coverage verification
4. **Code Style**: Detekt runs automatically; fix warnings before committing
5. **CI Pipeline**: Runs `detekt`, `domain:test`, and `jacocoTestCoverageVerification`

## EditorConfig

The project uses EditorConfig (`.editorconfig`) with:
- UTF-8 charset
- Kotlin: no trailing commas, wildcard imports for `java.util.*` and AssertJ
