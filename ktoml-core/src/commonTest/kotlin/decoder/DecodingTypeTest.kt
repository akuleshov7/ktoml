package decoder

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.exceptions.IllegalTomlTypeException
import com.akuleshov7.ktoml.exceptions.TomlCastException
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlin.test.Test
import kotlin.test.assertFailsWith

@Serializable
data class Bool(val a: Boolean)

@Serializable
data class B(val a: Byte)

@Serializable
data class F(val a: Float)

@Serializable
data class D(val a: Double)

@Serializable
data class I(val a: Int)

@Serializable
data class S(val a: Short)

@Serializable
data class L(val a: Long)

@Serializable
data class C(val a: Char)

@Serializable
data class Str(val a: String)

class DecodingTypeTest {
    @Test
    fun testExceptions() {
        assertFailsWith<IllegalTomlTypeException> { Toml.decodeFromString<S>("a = true") }
        assertFailsWith<IllegalTomlTypeException> { Toml.decodeFromString<B>("a = true") }
        assertFailsWith<IllegalTomlTypeException> { Toml.decodeFromString<F>("a = true") }
        assertFailsWith<IllegalTomlTypeException> { Toml.decodeFromString<I>("a = true") }
        assertFailsWith<IllegalTomlTypeException> { Toml.decodeFromString<C>("a = true") }

        assertFailsWith<TomlCastException> { Toml.decodeFromString<Bool>("a = \"test\"") }
        assertFailsWith<TomlCastException> { Toml.decodeFromString<Str>("a = true") }
        assertFailsWith<TomlCastException> { Toml.decodeFromString<L>("a = 12.0") }
        assertFailsWith<TomlCastException> { Toml.decodeFromString<D>("a = 1") }
    }
}
