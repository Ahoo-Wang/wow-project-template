# FluentAssert - Complete API Reference

## Project Information

**Name:** FluentAssert

**Version:** 0.2.6

**Description:** A Kotlin library providing fluent assertions for JDK types

**Language:** Kotlin

**Platform:** JVM (Java 17+ required)

**License:** Apache License 2.0

## Installation

### Maven

```xml
<dependency>
    <groupId>me.ahoo.test</groupId>
    <artifactId>fluent-assert-core</artifactId>
    <version>0.2.6</version>
    <scope>test</scope>
</dependency>
```

### Gradle (Kotlin DSL)

```kotlin
testImplementation("me.ahoo.test:fluent-assert-core:0.2.6")
```

## Core Extension Functions

All extension functions follow the pattern `Type.assert(): AssertJTypeAssert`, where:
- `Type` is any supported JDK type (nullable or non-nullable)
- `AssertJTypeAssert` is the corresponding AssertJ assertion class

### Import

```kotlin
import me.ahoo.test.asserts.assert
```

## Supported Types

### Primitives

| Type | Assertion Class |
|------|-----------------|
| `Boolean?` | `BooleanAssert` |
| `Byte?` | `ByteAssert` |
| `Short?` | `ShortAssert` |
| `Int?` | `IntegerAssert` |
| `Long?` | `LongAssert` |
| `Float?` | `FloatAssert` |
| `Double?` | `DoubleAssert` |
| `BigDecimal?` | `BigDecimalAssert` |

### Text

| Type | Assertion Class |
|------|-----------------|
| `String?` | `StringAssert` |

### Collections

| Type | Assertion Class |
|------|-----------------|
| `Iterable<T>?` | `IterableAssert<T>` |
| `Iterator<T>?` | `IteratorAssert<T>` |
| `Collection<T>?` | `CollectionAssert<T>` |
| `Array<T>?` | `ObjectArrayAssert<T>` |
| `List<T>?` | `ListAssert<T>` |
| `Map<K, V>?` | `MapAssert<K, V>` |
| `Optional<T>?` | `OptionalAssert<T>` |
| `Stream<T>?` | `ListAssert<T>` |

### Time/Date

| Type | Assertion Class |
|------|-----------------|
| `Date?` | `DateAssert` |
| `ZonedDateTime?` | `ZonedDateTimeAssert` |
| `LocalDateTime?` | `LocalDateTimeAssert` |
| `OffsetDateTime?` | `OffsetDateTimeAssert` |
| `OffsetTime?` | `OffsetTimeAssert` |
| `LocalTime?` | `LocalTimeAssert` |
| `LocalDate?` | `LocalDateAssert` |
| `YearMonth?` | `YearMonthAssert` |
| `Instant?` | `InstantAssert` |
| `Duration?` | `DurationAssert` |
| `Period?` | `PeriodAssert` |
| `Temporal?` | `TemporalAssert` |

### I/O

| Type | Assertion Class |
|------|-----------------|
| `Path?` | `PathAssert` |
| `File?` | `FileAssert` |
| `URL?` | `UrlAssert` |
| `URI?` | `UriAssert` |

### Concurrent

| Type | Assertion Class |
|------|-----------------|
| `Future<V>?` | `FutureAssert<V>` |
| `CompletableFuture<V>?` | `CompletableFutureAssert<V>` |
| `CompletionStage<V>?` | `CompletionStageAssert<V>` |

### Functional

| Type | Assertion Class |
|------|-----------------|
| `Predicate<T>?` | `PredicateAssert<T>` |

### Exceptions

| Type | Assertion Class |
|------|-----------------|
| `Throwable?` | `ThrowableAssert<Throwable>` |

## API Examples

### Primitives

#### Boolean

```kotlin
true.assert().isTrue()
false.assert().isFalse()
val nullableBool: Boolean? = null
nullableBool.assert().isNull()
```

#### Byte

```kotlin
val value: Byte = 42
value.assert().isEqualTo(42).isPositive()
```

#### Short

```kotlin
val value: Short = 1000
value.assert().isEqualTo(1000).isGreaterThan(0)
```

#### Int

```kotlin
val age = 25
age.assert().isEqualTo(25).isBetween(18, 65)
```

#### Long

```kotlin
val timestamp = System.currentTimeMillis()
timestamp.assert().isPositive().isGreaterThan(0)
```

#### Float

```kotlin
val pi = 3.14f
pi.assert().isEqualTo(3.14f).isPositive()
```

#### Double

```kotlin
val price = 19.99
price.assert().isEqualTo(19.99).isPositive()
```

#### BigDecimal

```kotlin
val amount = BigDecimal("123.45")
amount.assert().isEqualTo("123.45").isPositive()
```

### String

```kotlin
val name = "FluentAssert"
name.assert()
    .startsWith("Fluent")
    .endsWith("Assert")
    .contains("uentAss")
    .hasLength(11)
```

### Generic Types

#### Object

```kotlin
val person = Person("John", 30)
person.assert()
    .isNotNull()
    .hasFieldOrPropertyWithValue("name", "John")
```

#### Comparable

```kotlin
val version = "2.0.0"
version.assert()
    .isGreaterThan("1.0.0")
    .isLessThan("3.0.0")
```

### Collections

#### Iterable

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)
numbers.assert()
    .hasSize(5)
    .contains(3)
    .doesNotContain(6)
    .allMatch { it > 0 }
```

#### Iterator

```kotlin
val iterator = listOf(1, 2, 3).iterator()
iterator.assert().hasNext()
```

#### Collection

```kotlin
val set = setOf("apple", "banana", "orange")
set.assert()
    .hasSize(3)
    .contains("apple")
    .doesNotContain("grape")
```

#### Array

```kotlin
val array = arrayOf("a", "b", "c")
array.assert()
    .hasSize(3)
    .contains("b")
    .doesNotContain("d")
```

#### List

```kotlin
val items = listOf("apple", "banana", "orange")
items.assert()
    .hasSize(3)
    .contains("apple", "banana")
    .element(0).isEqualTo("apple")
```

#### Optional

```kotlin
val present = Optional.of("value")
present.assert()
    .isPresent()
    .contains("value")

val empty = Optional.empty<String>()
empty.assert().isEmpty()
```

#### Map

```kotlin
val map = mapOf("key1" to "value1", "key2" to "value2")
map.assert()
    .hasSize(2)
    .containsKey("key1")
    .containsValue("value1")
    .containsEntry("key1", "value1")
```

#### Stream

```kotlin
val stream = listOf(1, 2, 3, 4, 5).stream()
stream.assert()
    .hasSize(5)
    .contains(3)
    .allMatch { it > 0 }
```

### Time and Date

#### Date

```kotlin
val date = Date()
date.assert()
    .isToday()
    .isBefore(Date(System.currentTimeMillis() + 1000))
```

#### ZonedDateTime

```kotlin
val zonedDateTime = ZonedDateTime.now()
zonedDateTime.assert()
    .isToday()
    .hasZone(ZoneId.systemDefault())
```

#### Temporal

```kotlin
val instant = Instant.now()
instant.assert()
    .isBefore(Instant.now().plusSeconds(1))
```

#### LocalDateTime

```kotlin
val dateTime = LocalDateTime.now()
dateTime.assert()
    .isToday()
    .isBefore(LocalDateTime.now().plusHours(1))
```

#### OffsetDateTime

```kotlin
val offsetDateTime = OffsetDateTime.now()
offsetDateTime.assert()
    .isToday()
    .hasOffset(ZoneOffset.UTC)
```

#### OffsetTime

```kotlin
val offsetTime = OffsetTime.now()
offsetTime.assert()
    .isBefore(OffsetTime.now().plusHours(1))
```

#### LocalTime

```kotlin
val time = LocalTime.of(10, 30)
time.assert()
    .isBefore(LocalTime.of(12, 0))
    .hasHour(10)
    .hasMinute(30)
```

#### LocalDate

```kotlin
val date = LocalDate.of(2023, 12, 25)
date.assert()
    .hasYear(2023)
    .hasMonth(12)
    .hasDayOfMonth(25)
```

#### YearMonth

```kotlin
val yearMonth = YearMonth.of(2023, 12)
yearMonth.assert()
    .hasYear(2023)
    .hasMonth(12)
```

#### Instant

```kotlin
val instant = Instant.now()
instant.assert()
    .isBefore(Instant.now().plusSeconds(1))
```

#### Duration

```kotlin
val duration = Duration.ofHours(2)
duration.assert()
    .hasHours(2)
    .isGreaterThan(Duration.ofHours(1))
```

#### Period

```kotlin
val period = Period.of(1, 2, 3)
period.assert()
    .hasYears(1)
    .hasMonths(2)
    .hasDays(3)
```

### File System and I/O

#### Path

```kotlin
val path = Paths.get("/tmp/test.txt")
path.assert()
    .exists()
    .isReadable()
    .isRegularFile()
```

#### File

```kotlin
val file = File("/tmp/test.txt")
file.assert()
    .exists()
    .isFile()
    .canRead()
    .hasName("test.txt")
```

#### URL

```kotlin
val url = URL("https://example.com")
url.assert()
    .hasHost("example.com")
    .hasProtocol("https")
    .hasPort(443)
```

#### URI

```kotlin
val uri = URI("https://example.com/path?query=value")
uri.assert()
    .hasHost("example.com")
    .hasPath("/path")
    .hasQuery("query=value")
```

### Concurrent Programming

#### Future

```kotlin
val future = executor.submit(Callable { "result" })
future.assert()
    .isDone()
    .isNotCancelled()
```

#### CompletableFuture

```kotlin
val future = CompletableFuture.completedFuture("success")
future.assert()
    .isCompleted()
    .isCompletedWithValue("success")
```

#### CompletionStage

```kotlin
val stage = CompletableFuture.completedFuture("result")
stage.assert()
    .isCompleted()
    .isCompletedWithValue("result")
```

### Functional Programming

#### Predicate

```kotlin
val isEven = Predicate<Int> { it % 2 == 0 }
isEven.assert()
    .accepts(2, 4, 6)
    .rejects(1, 3, 5)
```

### Exception Testing

#### Throwable

```kotlin
val exception = RuntimeException("test error")
exception.assert()
    .hasMessage("test error")
    .isInstanceOf(RuntimeException::class.java)
```

#### assertThrownBy (Class parameter)

```kotlin
assertThrownBy(IllegalArgumentException::class.java) {
    throw IllegalArgumentException("invalid argument")
}.assert().hasMessage("invalid argument")
```

#### assertThrownBy (Reified)

```kotlin
assertThrownBy<IllegalArgumentException> {
    throw IllegalArgumentException("invalid argument")
}.assert().hasMessage("invalid argument")
```

## Comparison with AssertJ

| Feature | AssertJ | FluentAssert |
|---------|---------|-------------|
| Syntax | `assertThat(value).isEqualTo(expected)` | `value.assert().isEqualTo(expected)` |
| Null Safety | Manual null checks | Automatic null handling |
| Kotlin Integration | Java library | Kotlin-first design |
| Extension Functions | Not applicable | Full Kotlin extension support |
| Type Inference | Limited | Enhanced Kotlin type system |
| IDE Support | Good | Excellent (Kotlin-aware) |

## When to Use FluentAssert

- **Kotlin projects** - Better integration with Kotlin idioms
- **Null-heavy code** - Automatic null safety
- **Fluent style preference** - More readable assertion chains
- **Type-safe assertions** - Leverage Kotlin's type system

## When to Use AssertJ Directly

- **Java projects** - No need for Kotlin extensions
- **Custom assertion logic** - Direct AssertJ gives more control
- **Performance-critical code** - Minimal overhead