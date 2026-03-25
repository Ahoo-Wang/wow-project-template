---
name: fluent-assert
description: |
  Use FluentAssert for writing Kotlin test assertions. FluentAssert provides a fluent `.assert()` extension that wraps AssertJ with Kotlin idioms.
  
  Use this skill whenever writing assertions in Kotlin tests, especially for:
  - Unit tests with JUnit 5
  - Assertions for collections, strings, primitives, dates
  - Exception testing
  - Any JDK type assertions in Kotlin
  
  The library is available as `me.ahoo.test:fluent-assert-core`. Import with `import me.ahoo.test.asserts.assert`.
---

# FluentAssert - Kotlin Fluent Assertions

FluentAssert wraps AssertJ with Kotlin extension functions for more idiomatic, null-safe assertions.

## Quick Reference

```kotlin
import me.ahoo.test.asserts.assert

// Basic pattern: value.assert().assertionMethod()
42.assert().isGreaterThan(0)
"hello".assert().startsWith("hel")
listOf(1, 2, 3).assert().hasSize(3).contains(2)
```

## Core Pattern

**Instead of:**
```kotlin
import org.assertj.core.api.Assertions.assertThat
assertThat(value).isEqualTo(expected)
```

**Use:**
```kotlin
import me.ahoo.test.asserts.assert
value.assert().isEqualTo(expected)
```

## Supported Types

### Primitives
```kotlin
true.assert().isTrue()
25.assert().isGreaterThan(18).isLessThan(100)
BigDecimal("123.45").assert().isEqualTo("123.45")
```

### Strings
```kotlin
"FluentAssert".assert()
    .startsWith("Fluent")
    .endsWith("Assert")
    .contains("uentAss")
    .hasLength(11)
```

### Collections
```kotlin
listOf(1, 2, 3).assert().hasSize(5).contains(3)
setOf("a", "b").assert().hasSize(2).contains("a")
mapOf("key" to "value").assert().containsKey("key").containsValue("value")
Optional.of("value").assert().isPresent().contains("value")
arrayOf(1, 2, 3).assert().hasSize(3)
```

### Time/Date
```kotlin
Instant.now().assert().isBefore(Instant.now().plusSeconds(1))
LocalDate.of(2023, 12, 25).assert().hasYear(2023).hasMonth(12)
Duration.ofHours(2).assert().hasHours(2)
```

### Exception Testing
```kotlin
// Assert code throws exception
assertThrownBy<IllegalArgumentException> {
    throw IllegalArgumentException("invalid")
}.assert().hasMessage("invalid")

// Assert on existing exception
exception.assert().isInstanceOf(RuntimeException::class.java)
```

### I/O
```kotlin
Paths.get("/tmp/test.txt").assert().exists().isReadable()
File("/tmp/test.txt").assert().exists().isFile()
URL("https://example.com").assert().hasHost("example.com")
```

### Concurrency
```kotlin
CompletableFuture.completedFuture("result").assert().isCompletedWithValue("result")
```

## Null Safety

All extension functions accept nullable types. The `assert()` function is defined on `Type?`:

```kotlin
val nullableString: String? = null
nullableString.assert().isNull()

val nullableInt: Int? = 42
nullableInt.assert().isNotNull().isGreaterThan(0)
```

## Chaining

Method chaining works naturally - each assertion returns the AssertJ assertion object:

```kotlin
listOf(1, 2, 3, 4, 5).assert()
    .hasSize(5)
    .contains(3)
    .allMatch { it > 0 }
    .isSorted()
```

## Common Mistakes

### ❌ Don't use AssertJ directly
```kotlin
// Avoid
import org.assertj.core.api.Assertions.assertThat
assertThat(value).isEqualTo(expected)
```

### ✅ Use FluentAssert
```kotlin
// Prefer
import me.ahoo.test.asserts.assert
value.assert().isEqualTo(expected)
```

### ❌ Don't forget the import
The `.assert()` extension is NOT from AssertJ - it's from `me.ahoo.test.asserts.assert`.

## References

For complete API documentation, see:

- [`references/API.md`](references/API.md) - Concise overview with supported types and examples
- [`references/FULL-API.md`](references/FULL-API.md) - Complete API reference with all assertion methods

## Integration with Wow/Saga Tests

In Wow framework saga tests using `SagaSpec`:

```kotlin
import me.ahoo.test.asserts.assert
import me.ahoo.wow.test.SagaSpec

class DemoSagaSpec : SagaSpec<DemoSaga>({
    on {
        val demoCreated = DemoCreated("data")
        whenEvent(demoCreated) {
            expectNoError()
            expectCommandType(UpdateDemo::class)
            expectCommandBody<UpdateDemo> {
                data.assert().isEqualTo("updated")
            }
        }
    }
})
```

## When AssertJ Direct is Still Needed

FluentAssert delegates to AssertJ, so you can always access AssertJ methods:

```kotlin
// FluentAssert provides the extension
value.assert().isEqualTo(expected)

// But if you need specific AssertJ methods not exposed:
assertThat(value).usingRecursiveComparison().isEqualTo(expected)
```
