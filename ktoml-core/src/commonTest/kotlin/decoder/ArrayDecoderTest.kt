package decoder

import com.akuleshov7.ktoml.Toml
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

@Serializable
data class SimpleArray(val a: List<Int>)

@Serializable
data class NestedArray(val a: List<List<Int>>)

@Serializable
data class ArrayInInlineTable(val table: InlineTable)

@Serializable
data class InlineTable(
    val name: String,
    @SerialName("configurationList")
    val overriddenName: List<String>
)

@Serializable
data class TestArrays(
    @SerialName("configurationList1")
    val overriddenName1: List<String>,
    @SerialName("configurationList2")
    val overriddenName2: List<String>,
    val table: Table
)

@Serializable
data class Table(val name: String)

@Serializable
data class TestArraysAndSimple(
    val name1: String,
    @SerialName("configurationList1")
    val overriddenName1: List<String>,
    @SerialName("configurationList2")
    val overriddenName2: List<String>,
    val table: Table,
    val name2: String
)


class SimpleArrayDecoderTest {
    @Test
    fun testSimpleArrayDecoder() {
        val test = "a = [1, 2,      3]"
        assertEquals(SimpleArray(listOf(1, 2, 3)), Toml.decodeFromString(test))
    }

    @Test
    fun testSimpleArrayDecoderInNestedTable() {
        var test = """
            |[table]
            |name = "my name"
            |configurationList = ["a",  "b",  "c"]
            """.trimMargin()

        assertEquals(
            ArrayInInlineTable(
                table = InlineTable(name = "my name", overriddenName = listOf("a", "b", "c"))
            ), Toml.decodeFromString(test)
        )

        test =
            """
            |[table]
            |configurationList = ["a",  "b",  "c"]
            |name = "my name"
            """.trimMargin()

        assertEquals(
            ArrayInInlineTable(
                table = InlineTable(name = "my name", overriddenName = listOf("a", "b", "c"))
            ), Toml.decodeFromString(test)
        )

        val testTable = """
            |configurationList1 = ["a",  "b",  "c"]
            |configurationList2 = ["a",  "b",  "c"]
            |[table]
            |name = "my name"
            """.trimMargin()

        assertEquals(
            TestArrays(
                overriddenName1 = listOf("a", "b", "c"),
                overriddenName2 = listOf("a", "b", "c"),
                table = Table(name = "my name")
            ), Toml.decodeFromString(testTable)
        )

        val testTableAndVariables = """
            |name1 = "simple"
            |configurationList1 = ["a",  "b",  "c"]
            |name2 = "simple"
            |configurationList2 = ["a",  "b",  "c"]
            |[table]
            |name = "my name"
            """.trimMargin()


        assertEquals(
            TestArraysAndSimple(
                name1 = "simple", overriddenName1 = listOf("a", "b", "c"),
                overriddenName2 = listOf("a", "b", "c"), table = Table(name = "my name"), name2 = "simple"
            ), Toml.decodeFromString(testTableAndVariables)
        )
    }

    @Test
    @Ignore
    fun testNestedArrayDecoder() {
        // FixMe: nested array decoding causes issues and is not supported yet
        val test = "a = [[1, 2],      [3,  4]]"
        assertEquals(NestedArray(listOf(listOf(1, 2), listOf(3, 4))), Toml.decodeFromString(test))
    }
}
