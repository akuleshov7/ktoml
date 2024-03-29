package com.akuleshov7.ktoml.encoders

import com.akuleshov7.ktoml.Toml
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlin.jvm.JvmInline
import kotlin.test.Test
import kotlin.test.assertEquals

class ValueClassEncoderTest {

    @Serializable
    @JvmInline
    value class Color(val rgb: Int)

    @Serializable
    data class NamedColor(val color: Color, val name: String)

    @Test
    fun testForSimpleValueClass() {
        val color = Color(15)
        val result = Toml.encodeToString(color)

        assertEquals("rgb = 15", result)
    }

    @Test
    fun testForNestedValueClass() {
        val namedColor = NamedColor(
            Color(150),
            "black"
        )

        val result = Toml.encodeToString(namedColor)
        assertEquals(
            """
                color = 150
                name = "black"
            """.trimIndent(),
            result
        )
    }

    @Test
    fun testForLisOfValueClass() {
        @Serializable
        class Palette(val colors: List<Color>)
        val palette = Palette(
            listOf(
                Color(0),
                Color(255),
                Color(128),
            )
        )

        val result = Toml.encodeToString(palette)
        assertEquals("colors = [ 0, 255, 128 ]", result)
    }

    @Serializable
    @JvmInline
    value class Num(val int: Int)

    @Serializable
    data class Nums(
        val num1: Num,
        val num2: Num,
    )

    @Test
    fun testForMultipleValueClass() {
        val nums = Nums(
            num1 = Num(5),
            num2 = Num(111)
        )
        val result = Toml.encodeToString(nums)

        assertEquals(
            """
                num1 = 5
                num2 = 111
            """.trimIndent(),
            result
        )
    }

    @Serializable
    data class MyObject(
        val height: Int,
        val width: Int
    )

    @Serializable
    @JvmInline
    value class Info(val obj: MyObject)

    @Serializable
    data class InfoWrapper(
        val metaInfo1: Int,
        val info: Info,
        val metaInfo2: String
    )

    @Test
    fun testForValueClassWithObjectInside() {
        val obj = InfoWrapper(
            metaInfo1 = 1,
            info = Info(MyObject(10, 20)),
            metaInfo2 = "test"
        )
        val result = Toml.encodeToString(obj)

        assertEquals(
            """
                metaInfo1 = 1
                metaInfo2 = "test"
                
                [info]
                    height = 10
                    width = 20
            """.trimIndent(),
            result
        )
    }

    @Serializable
    @JvmInline
    value class AnotherInfoWrapper(val info: Info)

    @Test
    fun testForValueClassInsideValueClass() {
        val obj = AnotherInfoWrapper(Info(MyObject(10, 20)))
        val result = Toml.encodeToString(obj)

        assertEquals(
            """
                [info]
                    height = 10
                    width = 20
            """.trimIndent(),
            result
        )
    }

    @Test
    fun testDataClassInsideValueClass() {
        val obj = Info(MyObject(32, 64))
        val result = Toml.encodeToString(obj)

        assertEquals(
            """
                [obj]
                    height = 32
                    width = 64
            """.trimIndent(),
            result
        )
    }
}
