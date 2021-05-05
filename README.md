## ktoml 

Native and multiplatform Kotlin serialization library for serialization/deserialization of [toml](https://toml.io/en/) format. 

## How to use
**Deserialization:**
```kotlin
import deserialize
@Serializable
data class MyClass(/* your fields */)

val result = deserialize<MyClass>(/* */)
```

## How it works

This tool natively deserialize toml expressions using native Kotlin compiler plug-in and [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serialization-guide.md).

from toml test:
```text
e = 777

[table1]
a = 5
b = 6

[table2]
a = 5

    [table2.inlineTable]
        a = "a"
        b = A
   
```

to `MyClass`
```kotlin
    enum class TestEnum {
        A, B, C
    }

    @Serializable
    data class MyClass(val e: Int, val table1: Table1, val table2: Table2)

    @Serializable
    data class Table1(val a: Int, val b: Int)

    @Serializable
    data class Table2(val a: Int, val inlineTable: InlineTable)

    @Serializable
    data class InlineTable(val a: String, val b: TestEnum)
```

Or in json-terminology:
```json
        {
          "table1": {
            "a": 5,
            "b": 5
            },
          "table2": {
             "a": 5,
             "inlineTable": {
                 "a": "a",
                  "b": A
            }
          }
        }
``` 