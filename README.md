## <img src="/ktoml.png" width="300px"/>

![Releases](https://img.shields.io/github/v/release/akuleshov7/ktoml)
![Maven Central](https://img.shields.io/maven-central/v/com.akuleshov7/ktoml-core)
![License](https://img.shields.io/github/license/akuleshov7/ktoml)
![Build and test](https://github.com/akuleshov7/ktoml/actions/workflows/build_and_test.yml/badge.svg?branch=main)
![Lines of code](https://img.shields.io/tokei/lines/github/akuleshov7/ktoml)
![Hits-of-Code](https://hitsofcode.com/github/akuleshov7/ktoml?branch=main)
![GitHub repo size](https://img.shields.io/github/repo-size/akuleshov7/ktoml)
![codebeat badge](https://codebeat.co/badges/0518ea49-71ed-4bfd-8dd3-62da7034eebd)
![maintainability](https://api.codeclimate.com/v1/badges/c75d2d6b0d44cea7aefe/maintainability)
![Run deteKT](https://github.com/akuleshov7/ktoml/actions/workflows/detekt.yml/badge.svg?branch=main)
![Run diKTat](https://github.com/akuleshov7/ktoml/actions/workflows/diktat.yml/badge.svg?branch=main)

Fully Native and Multiplatform Kotlin serialization library for serialization/deserialization of [toml](https://toml.io/en/) format.
Uses native [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization), provided by Kotlin. This library contains no Java code and no Java dependencies.
We believe that TOML is actually the most readable and user-friendly **configuration file** format.
So we decided to support this format for the `kotlinx` serialization library.

## Contribution
As this young and big project [is needed](https://github.com/Kotlin/kotlinx.serialization/issues/1092) by the Kotlin community, we need your help.
We will be glad if you will test `ktoml` or contribute to this project.
In case you don't have much time for this - at least spend 5 seconds to give us a star to attract other contributors!

**Thanks!** :pray: :partying_face:

## Acknowledgement
Special thanks to those awesome developers who give us great suggestions, help us to maintain and improve this project:
@NightEule5, @Peanuuutz, @petertrr, @Olivki and @edrd-f.

## Supported platforms
All the code is written in Kotlin **common** module. This means that it can be built for each and every Kotlin native platform.
However, to reduce the scope, ktoml now supports only the following platforms:
- jvm
- mingwx64
- linuxx64
- macosx64
- js (only for ktoml-core). Note, that `js(LEGACY)` is [not supported](https://github.com/Kotlin/kotlinx.serialization/issues/1448)

Other platforms could be added later on the demand (just create a corresponding issue) or easily built by users on their machines.

:globe_with_meridians: ktoml supports Kotlin 1.6

## Current limitations
:heavy_exclamation_mark: Please note, that TOML standard does not define Java-like types: `Char`, `Short`, etc.
You can check types that are supported in TOML [here](https://toml.io/en/v1.0.0#string).
We will support all Kotlin primitive types in the future with the non-strict configuration of ktoml, but now
only String, Long, Double and Boolean are supported from the list of Kotlin primitives.

**General** \
We are still developing and testing this library, so it has several limitations: \
:white_check_mark: deserialization (with some parsing limitations) \
:x: serialization (not implemented [yet](https://github.com/akuleshov7/ktoml/issues/11), less important for TOML config-files)

**Parsing** \
:white_check_mark: Table sections (single and dotted) \
:white_check_mark: Key-value pairs (single and dotted) \
:white_check_mark: Integer type \
:white_check_mark: Float type \
:white_check_mark: String type \
:white_check_mark: Float type \
:white_check_mark: Boolean type \
:white_check_mark: Simple Arrays \
:white_check_mark: Comments \
:white_check_mark: Literal Strings \
:white_check_mark: Inline Tables \
:x: Arrays: nested; multiline; of Different Types \
:x: Multiline Strings \
:x: Nested Inline Tables \
:x: Array of Tables \
:x: Inline Array of Tables \
:x: Offset Date-Time, Local: Date-Time; Date; Time

## Dependency
The library is hosted on the [Maven Central](https://search.maven.org/artifact/com.akuleshov7/ktoml-core).
To import `ktoml` library you need to add following dependencies to your code:
<details>
<summary>Maven</summary>

```pom
<dependency>
  <groupId>com.akuleshov7</groupId>
  <artifactId>ktoml-core</artifactId>
  <version>0.2.11</version>
</dependency>
<dependency>
  <groupId>com.akuleshov7</groupId>
  <artifactId>ktoml-file</artifactId>
  <version>0.2.11</version>
</dependency>
```
</details>

<details>
<summary>Gradle Groovy</summary>

```groovy
implementation 'com.akuleshov7:ktoml-core:0.2.11'
implementation 'com.akuleshov7:ktoml-file:0.2.11'
```
</details>

<details>
<summary>Gradle Kotlin</summary>

```kotlin
implementation("com.akuleshov7:ktoml-core:0.2.11")
implementation("com.akuleshov7:ktoml-file:0.2.11")
```
</details>

## How to use
:heavy_exclamation_mark: as TOML is a foremost language for config files, we have also supported the deserialization from file.
However, we are using [okio](https://github.com/square/okio) to read the file, so it will be added as a dependency to your
project if you will import [ktoml-file](https://search.maven.org/artifact/com.akuleshov7/ktoml-file).
For basic scenarios of decoding strings you can simple use [ktoml-core](https://search.maven.org/artifact/com.akuleshov7/ktoml-core).

:heavy_exclamation_mark: don't forget to add the serialization plugin `kotlin("plugin.serialization")` to your project.
Otherwise, `@Serialization` annotation won't work properly.

**Deserialization:**
<details>
<summary>Straight-forward deserialization</summary>

```kotlin
// add extensions from 'kotlinx' lib to your project:
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.serializer
// add com.akuleshov7:ktoml-core to your project:
import com.akuleshov7.ktoml.deserialize

@Serializable
data class MyClass(/* your fields */)

// to deserialize toml input in a string format (separated by newlines '\n')
// no need to provide serializer() explicitly if you will use extension method from
// <kotlinx.serialization.decodeFromString>
val resultFromString = Toml.decodeFromString<MyClass>(/* string with a toml input */)
val resultFromList = Toml.decodeFromString<MyClass>(serializer(), /* list with lines of strings with a toml input */)
```
</details>

<details>
<summary>Partial deserialization</summary>

Partial Deserialization can be useful when you would like to deserialize only **one single** table and you do not want
to reproduce whole object structure in your code.

```kotlin
// If you need to deserialize only some part of the toml - provide the full name of the toml table. 
// The deserializer will work only with this table and it's children.
// For example if you have the following toml, but you want only to decode [c.d.e.f] table: 
// [a]
//   b = 1
// [c.d.e.f]
//   d = "5"

val result = Toml.partiallyDecodeFromString<MyClassOnlyForTable>(serializer(), /* string with a toml input */, "c.d.e.f")
val result = Toml.partiallyDecodeFromString<MyClassOnlyForTable>(serializer(), /* list with toml strings */, "c.d.e.f")
```
</details>

<details>
<summary>Toml File deserialization</summary>

```kotlin
// add com.akuleshov7:ktoml-file to your project
import com.akuleshov7.ktoml.file

val resultFromString = TomlFileReader.decodeFromFile<MyClass>(serializer(), /* file path to toml file */)
val resultFromList = TomlFileReader.partiallyDecodeFromFile<MyClass>(serializer(),  /* file path to toml file */, /* table name */)
```
</details>

**Parser to AST:**
<details>
<summary>Simple parser</summary>

```kotlin
import com.akuleshov7.ktoml.parsers.TomlParser
import com.akuleshov7.ktoml.TomlConfig
/* ========= */
var tomlAST = TomlParser(TomlConfig()).parseStringsToTomlTree(/* list with toml strings */)
tomlAST = TomlParser(TomlConfig()).parseString(/* the string that you want to parse */)
tomlAST.prettyPrint()
```
</details>

### Configuration
Ktoml parsing and deserialization was made configurable to fit all the requirements from users. We have created a
special configuration class that can be passed to the decoder method:

```kotlin
Toml(
    config = TomlConfig(
        // allow/prohibit unknown names during the deserialization, default false
        ignoreUnknownNames = false,
        // allow/prohibit empty values like "a = # comment", default true
        allowEmptyValues = true,
        // allow/prohibit null values like "a = null", default true
        allowNullValues = true,
        // allow/prohibit escaping of single quotes in literal strings, default true
        allowEscapedQuotesInLiteralStrings = true,
        // allow/prohibit processing of empty toml, if false - throws an InternalDecodingException exception, default is true
        allowEmptyToml = true,
        // indentation symbols for serialization, default 4 spaces
        indentation = Indentation.FOUR_SPACES,
    )
).decodeFromString<MyClass>(
    tomlString
)
```

## Exceptions
Ktoml will produce different exceptions in case of the invalid input. Please note, that some of strict checks can be enabled or disabled (please see
`Configuration` section of this readme). We intentionally made only two parental sealed exceptions public:
`TomlDecodingException` and `TomlEncodingException` - you can catch them in your code. All other exceptions inherit one of these two and will not be public.

## How ktoml works: examples

This tool natively deserializes toml expressions using native Kotlin compiler plug-in and [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serialization-guide.md).

The following example:
```toml
someBooleanProperty = true
# inline tables in gradle 'libs.versions.toml' notation
gradle-libs-like-property = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }

[table1]
# it can be null or nil, but don't forget to mark it with '?' in the codes
# keep in mind, that null is prohibited by TOML spec, but it is very important in Kotlin
# see allowNullValues for a more strict enforcement of the TOML spec
property1 = null
property2 = 6
# check property3 in Table1 below. As it has the default value, it is not required and can be not provided 
 
[table2]
someNumber = 5
   [table2."akuleshov7.com"]
       name = 'this is a "literal" string'
       # empty lists are also supported
       configurationList = ["a",  "b",  "c", null]

# such redeclaration of table2
# is prohibited in toml specification;
# but ktoml is allowing it in non-strict mode: 
[table2]       
otherNumber = 5.56

```

can be deserialized to `MyClass`:
```kotlin
@Serializable
data class MyClass(
    val someBooleanProperty: Boolean,
    val table1: Table1,
    val table2: Table2,
   @SerialName("gradle-libs-like-property")
   val kotlinJvm: GradlePlugin
)

@Serializable
data class Table1(
    // nullable values, from toml you can pass null/nil/empty value to this kind of a field
    val property1: Long?,
    // please note, that according to the specification of toml integer values should be represented with Long
    val property2: Long,
    // no need to pass this value as it has the default value and is NOT REQUIRED
    val property3: Long = 5
)

@Serializable
data class Table2(
    val someNumber: Long,
    @SerialName("akuleshov7.com")
    val inlineTable: InlineTable,
    val otherNumber: Double
)

@Serializable
data class GradlePlugin(val id: String, val version: Version)

@Serializable
data class Version(val ref: String)

```

with the following code:
```kotlin
Toml.decodeFromString<MyClass>(/* your toml string */)
```

Translation of the example above to json-terminology:

```json
{
  "someBooleanProperty": true,
  "table1": {
    "property1": 5,
    "property2": 5
  },
  "table2": {
    "someNumber": 5,
    "akuleshov7.com": {
      "name": "my name",
      "configurationList": [
        "a",
        "b",
        "c"
      ],
      "otherNumber": 5.56
    }
  },
  "gradle-libs-like-property": {
    "id": "org.jetbrains.kotlin.jvm",
    "version": {
      "ref": "kotlin"
    }
  }
}

``` 

:heavy_exclamation_mark: You can check how this example works in [ReadMeExampleTest](https://github.com/akuleshov7/ktoml/blob/main/ktoml-core/src/commonTest/kotlin/com/akuleshov7/ktoml/decoders/ReadMeExampleTest.kt).
