package com.akuleshov7.ktoml.parsers.node

import com.akuleshov7.ktoml.exceptions.TomlParsingException
import com.akuleshov7.ktoml.parsers.splitKeyToTokens
import com.akuleshov7.ktoml.parsers.trimQuotes

/**
 * Class that represents a toml key-value pair.
 * Key has TomlKey type, Value has TomlValue type
 *
 * @property rawContent
 * @property lineNo
 */
class TomlKey(val rawContent: String, val lineNo: Int) {
    val keyParts = rawContent.splitKeyToTokens()
    val content = keyParts.last().trimQuotes().trim()
    val isDotted = isDottedKey()

    init {
        validateQuotes()
        validateSymbols()
    }

    /**
     * small validation for quotes: each quote should be closed in a key
     */
    private fun validateQuotes() {
        if (rawContent.count { it == '\"' } % 2 != 0 || rawContent.count { it == '\'' } % 2 != 0) {
            throw TomlParsingException(
                "Not able to parse the key: [$rawContent] as it does not have closing quote." +
                        " Please note, that you cannot use even escaped quotes in the bare keys.",
                lineNo
            )
        }
    }

    /**
     * validate that bare key parts (not quoted) contain only valid symbols A..Z, a..z, 0..9, -, _
     */
    private fun validateSymbols() {
        var singleQuoteIsClosed = true
        var doubleQuoteIsClosed = true
        rawContent.trimQuotes().forEach { ch ->
            when (ch) {
                '\'' -> singleQuoteIsClosed = !singleQuoteIsClosed
                '\"' -> doubleQuoteIsClosed = !doubleQuoteIsClosed
                else -> {
                    // this is a generated else block
                }
            }
            if (doubleQuoteIsClosed && singleQuoteIsClosed &&
                    // FixMe: isLetterOrDigit is not supported in Kotlin 1.4, but 1.5 is not compiling right now
                    !setOf('_', '-', '.', '"', '\'', ' ', '\t').contains(ch) && !ch.isLetterOrDigit()) {
                throw TomlParsingException(
                    "Not able to parse the key: [$rawContent] as it contains invalid symbols." +
                            " In case you would like to use special symbols - use quotes.",
                    lineNo
                )
            }
        }
    }

    private fun Char.isLetterOrDigit() = CharRange('A', 'Z').contains(this) ||
            CharRange('a', 'z').contains(this) ||
            CharRange('0', '9').contains(this)

    /**
     * checking that we face a key in the following format: a."ab.c".my-key
     *
     * @return true if the key is in dotted format (a.b.c)
     */
    private fun isDottedKey(): Boolean {
        var singleQuoteIsClosed = true
        var doubleQuoteIsClosed = true
        rawContent.forEach { ch ->
            when (ch) {
                '\'' -> singleQuoteIsClosed = !singleQuoteIsClosed
                '\"' -> doubleQuoteIsClosed = !doubleQuoteIsClosed
                else -> {
                    // this is a generated else block
                }
            }

            if (ch == '.' && doubleQuoteIsClosed && singleQuoteIsClosed) {
                return true
            }
        }
        return false
    }
}
