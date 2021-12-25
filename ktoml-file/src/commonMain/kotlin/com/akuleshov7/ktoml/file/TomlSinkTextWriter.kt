package com.akuleshov7.ktoml.file

import com.akuleshov7.ktoml.KtomlConf
import com.akuleshov7.ktoml.writers.AbstractTomlTextWriter
import okio.BufferedSink
import okio.Closeable

internal class TomlSinkTextWriter(
    private val sink: BufferedSink,
    ktomlConf: KtomlConf
) : AbstractTomlTextWriter(ktomlConf), Closeable {
    override fun emit(fragment: String) {
        sink.writeUtf8(fragment)
    }

    override fun emit(fragment: Char) {
        sink.writeUtf8CodePoint(fragment.code)
    }

    override fun emit(fragment1: String, fragment2: String) {
        sink.writeUtf8(fragment1)
            .writeUtf8(fragment2)
    }

    override fun emit(fragment1: String, fragment2: String, fragment3: String) {
        sink.writeUtf8(fragment1)
            .writeUtf8(fragment2)
            .writeUtf8(fragment3)
    }

    override fun emit(fragment1: Char, fragment2: String, fragment3: Char) {
        sink.writeUtf8CodePoint(fragment1.code)
            .writeUtf8(fragment2)
            .writeUtf8CodePoint(fragment3.code)
    }

    // Closeable

    override fun close(): Unit = sink.close()
}