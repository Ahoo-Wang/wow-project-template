# FluentAssert

## Project Overview

FluentAssert is a Kotlin library that provides fluent assertions for JDK types, making your tests more readable and expressive. The library wraps AssertJ assertions with Kotlin extension functions for better syntax.

**Key Features:**

- Fluent API for chaining assertions
- Null-safe operations for all types
- Comprehensive JDK type coverage
- Kotlin-first design with excellent IDE support
- Zero runtime dependencies (only AssertJ)

**Version:** 0.2.6

**Language:** Kotlin

**Platform:** JVM (Java 17+)

**License:** Apache License 2.0

## Quick Start

```kotlin
import me.ahoo.test.asserts.assert

// Basic usage
"hello".assert().startsWith("h").endsWith("o")
42.assert().isGreaterThan(0).isLessThan(100)
listOf(1, 2, 3).assert().hasSize(3).contains(2)
```

## Core API

### Extension Functions Pattern

All assertions follow the pattern: `value.assert().assertionMethod(parameters)`

### Supported Types

- **Primitives:** Boolean, Byte, Short, Int, Long, Float, Double, BigDecimal
- **Text:** String
- **Collections:** Iterable<T>, Iterator<T>, Collection<T>, Array<T>, List<T>, Map<K,V>, Optional<T>, Stream<T>
- **Time/Date:** Date, ZonedDateTime, LocalDateTime, OffsetDateTime, OffsetTime, LocalTime, LocalDate, YearMonth, Instant, Duration, Period, Temporal
- **I/O:** Path, File, URL, URI
- **Concurrent:** Future<V>, CompletableFuture<V>, CompletionStage<V>
- **Functional:** Predicate<T>
- **Exceptions:** Throwable

## Project Structure

```
FluentAssert/
├── core/src/main/kotlin/me/ahoo/test/asserts/
│   ├── Jdk.kt          # Core JDK type assertions
│   ├── JdkConcurrent.kt # Concurrent programming assertions
│   ├── JdkFunction.kt  # Functional programming assertions
│   ├── JdkIO.kt        # I/O assertions
│   ├── JdkTime.kt      # Time/date assertions
│   └── Throwable.kt     # Exception testing
├── core/src/test/kotlin/ # Comprehensive test suite
├── dependencies/       # Dependency management
├── config/             # Build and analysis configuration
└── build.gradle.kts    # Build configuration
```

## Development

### Requirements

- Java 17+
- Kotlin 1.8.0+
- Gradle 7.0+

### Build Commands

```bash
./gradlew build    # Full build
./gradlew test     # Run tests
./gradlew detekt   # Code analysis
./gradlew jacocoTestReport  # Coverage report
```

### Testing

- JUnit 5 for test execution
- AssertJ for assertions
- Comprehensive test coverage
- Null-safety testing for all types

## Key Design Principles

1. **Fluent Interface:** Method chaining for readable assertions
2. **Null Safety:** All functions handle nullable types appropriately
3. **Type Safety:** Full Kotlin type system integration
4. **Performance:** Minimal overhead, delegates to optimized AssertJ
5. **Consistency:** Same API patterns across all supported types

## Usage Examples

### Collections

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)
numbers.assert()
    .hasSize(5)
    .contains(3)
    .allMatch { it > 0 }
```

### Exception Testing

```kotlin
assertThrownBy<IllegalArgumentException> {
    throw IllegalArgumentException("error")
}.assert().hasMessage("error")
```

### Time Assertions

```kotlin
LocalDate.now().assert().isToday()
Instant.now().assert().isBefore(Instant.now().plusSeconds(1))
```

## Contributing

See CONTRIBUTING.md for detailed contribution guidelines.

## License

Apache License 2.0 - see LICENSE file for details.