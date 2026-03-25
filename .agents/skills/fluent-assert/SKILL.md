---
name: fluent-assert
description: |
  Use FluentAssert for writing Kotlin test assertions. FluentAssert provides a fluent `.assert()` extension that wraps AssertJ with Kotlin idioms.

  MANDATORY whenever writing Kotlin tests with assertions. This includes:
  - All JUnit 5, JUnit 4, or other Kotlin test frameworks
  - Any assertion on JDK types (collections, strings, primitives, dates, exceptions, etc.)
  - Wow framework saga tests with expectCommandBody blocks
  - Exception testing with assertThrownBy<>

  CRITICAL: Always use `import me.ahoo.test.asserts.assert` - NOT AssertJ's assertThat() which is verbose and not null-safe in Kotlin.

  The library is available as `me.ahoo.test:fluent-assert-core`.
---

# FluentAssert - Kotlin Fluent Assertions

## Table of Contents
- [Quick Reference](#quick-reference)
- [Supported Types](#supported-types)
- [Null Safety](#null-safety)
- [Chaining](#chaining)
- [Common Mistakes](#common-mistakes)
- [References](#references)
- [Integration with Wow/Saga Tests](#integration-with-wowsaga-tests)
- [When AssertJ Direct is Still Needed](#when-assertj-direct-is-still-needed)

---

## Quick Reference

```kotlin
import me.ahoo.test.asserts.assert

// Basic pattern: value.assert().assertionMethod()
42.assert().isGreaterThan(0)
"hello".assert().startsWith("hel")
listOf(1, 2, 3).assert().hasSize(3).contains(2)
```

**Core Pattern:**

| Approach | Code |
|----------|------|
| ❌ Avoid | `assertThat(value).isEqualTo(expected)` |
| ✅ Prefer | `value.assert().isEqualTo(expected)` |

---

## Supported Types

### Primitives
```kotlin
true.assert().isTrue()
false.assert().isFalse()
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
    .doesNotContain("Java")    // negation
    .matches("^Fluent.*$")     // regex
```

### Collections
```kotlin
listOf(1, 2, 3).assert()
    .hasSize(3)
    .contains(1, 2, 3)
    .doesNotContain(4, 5)
    .containsExactly(1, 2, 3)  // exact order
    .element(0).isEqualTo(1)   // access by index

setOf("a", "b").assert()
    .hasSize(2)
    .contains("a")
    .doesNotContain("c")

mapOf("key" to "value").assert()
    .hasSize(1)
    .containsKey("key")
    .containsValue("value")
    .containsEntry("key", "value")
    .allSatisfy { k, v -> k.assert().isNotBlank(); v.assert().isNotBlank() }

Optional.of("value").assert()
    .isPresent()
    .contains("value")
    .isEmpty()                 // for empty Optional

arrayOf(1, 2, 3).assert()
    .hasSize(3)
    .contains(2)
```

### Time/Date
```kotlin
Instant.now().assert().isBefore(Instant.now().plusSeconds(1))
LocalDate.of(2023, 12, 25).assert().hasYear(2023).hasMonth(12)
LocalDateTime.now().assert().isToday()
Duration.ofHours(2).assert().hasHours(2).isGreaterThan(Duration.ofHours(1))
```

### Exception Testing
```kotlin
// Assert code throws exception
assertThrownBy<IllegalArgumentException> {
    throw IllegalArgumentException("invalid")
}.assert().hasMessage("invalid")

// Assert on existing exception
exception.assert()
    .isInstanceOf(RuntimeException::class.java)
    .hasMessageContaining("error")
    .hasCauseInstanceOf(NullPointerException::class.java)
```

### I/O
```kotlin
Paths.get("/tmp/test.txt").assert().exists().isReadable().isRegularFile()
File("/tmp/test.txt").assert().exists().isFile().canRead().hasName("test.txt")
URL("https://example.com").assert().hasHost("example.com").hasProtocol("https")
```

### Concurrency
```kotlin
CompletableFuture.completedFuture("result").assert()
    .isCompleted()
    .isCompletedWithValue("result")
    .isCompletedExceptionally()
```

### Type Checking
```kotlin
val obj: Any = "string"
obj.assert()
    .isInstanceOf(String::class.java)
    .isNotInstanceOf(Int::class.java)
```

---

## Null Safety

All extension functions accept nullable types. The `assert()` function is defined on `Type?`:

```kotlin
val nullableString: String? = null
nullableString.assert().isNull()

val nullableInt: Int? = 42
nullableInt.assert().isNotNull().isGreaterThan(0)
```

---

## Chaining

Method chaining works naturally - each assertion returns the AssertJ assertion object:

```kotlin
listOf(1, 2, 3, 4, 5).assert()
    .hasSize(5)
    .contains(3)
    .allMatch { it > 0 }
    .isSorted()
    .doesNotContain { it < 0 }
```

**Recommended assertion order:**
1. `hasSize()` or `isNotEmpty()` first - set context
2. `contains()` / `doesNotContain()` next - main checks
3. `allMatch()` / `anySatisfy()` last - conditions

---

## Common Mistakes

### ❌ Wrong Import
```kotlin
// WRONG - verbose, not null-safe in Kotlin
import org.assertj.core.api.Assertions.assertThat
assertThat(value).isEqualTo(expected)
```

### ✅ Correct Import
```kotlin
// CORRECT - idiomatic Kotlin
import me.ahoo.test.asserts.assert
value.assert().isEqualTo(expected)
```

### ❌ Incorrect Null Assertion
```kotlin
val name: String? = null
name.assert().isNotNull()  // FAILS - name is null
name.assert().isNull()     // CORRECT - asserts null
```

### ❌ Wrong Assertion Order
```kotlin
// Works but less readable
listOf(1, 2, 3).assert().contains(2).hasSize(3)

// Better - context first, then details
listOf(1, 2, 3).assert().hasSize(3).contains(2)
```

### ❌ Using AssertJ Directly
```kotlin
// AVOID - don't mix styles
assertThat(value).isEqualTo(expected)
value.assert().isGreaterThan(0)
```

---

## References

For complete API documentation, see:

- [`references/API.md`](references/API.md) - Concise overview with supported types and examples
- [`references/FULL-API.md`](references/FULL-API.md) - Complete API reference with all assertion methods

---

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
                data.assert().isNotNull().isEqualTo("updated")
            }
        }
    }
})
```

---

## When AssertJ Direct is Still Needed

FluentAssert delegates to AssertJ, so you can always access AssertJ methods:

```kotlin
// FluentAssert provides the extension
value.assert().isEqualTo(expected)

// For specific AssertJ methods not exposed via extension:
assertThat(value).usingRecursiveComparison().isEqualTo(expected)
```

---

## Exceptions

All AssertJ exception assertion methods are available via `.assert()`:

```kotlin
throwable.assert()
    .hasMessage("message")
    .hasMessageContaining("part")
    .hasMessageMatching("regex")
    .hasNoCause()
    .hasCauseInstanceOf(Cause::class.java)
    .isInstanceOf(Exception::class.java)
    .isNotInstanceOf(Error::class.java)
```