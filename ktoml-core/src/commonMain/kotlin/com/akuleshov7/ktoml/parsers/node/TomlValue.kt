/**
 * All representations of TOML value nodes are stored in this file
 */

package com.akuleshov7.ktoml.parsers.node

import com.akuleshov7.ktoml.TomlConfig
import com.akuleshov7.ktoml.exceptions.TomlParsingException
import com.akuleshov7.ktoml.parsers.trimBrackets
import com.akuleshov7.ktoml.parsers.trimQuotes
import com.akuleshov7.ktoml.parsers.trimSingleQuotes

/**
 * Base class for all nodes that represent values
 * @property lineNo - line number of original file
 */
public sealed class TomlValue(public val lineNo: Int) {
    public abstract var content: Any
}

/**
 * Toml AST Node for a representation of literal string values: key = 'value' (with single quotes and no escaped symbols)
 * The only difference from the TOML specification (https://toml.io/en/v1.0.0) is that we will have one escaped symbol -
 * single quote and so it will be possible to use a single quote inside.
 */
public class TomlLiteralString(
    content: String,
    lineNo: Int,
    tomlConfig: TomlConfig = TomlConfig()) : TomlValue(lineNo) {
    override var content: Any = if (content.startsWith("'") && content.endsWith("'")) {
        val contentString = content.trimSingleQuotes()
        if (tomlConfig.escapedQuotesInLiteralStringsAllowed) contentString.convertSingleQuotes() else contentString
    } else {
        throw TomlParsingException(
            "Literal string should be wrapped with single quotes (''), it looks that you have forgotten" +
                    " the single quote in the end of the following string: <$content>", lineNo
        )
    }

    /**
     * According to the TOML standard (https://toml.io/en/v1.0.0#string) single quote is prohibited.
     * But in ktoml we don't see any reason why we cannot escape it. Anyway, by the TOML specification we should fail, so
     * why not to try to handle this situation at least somehow.
     *
     * Conversion is done after we have trimmed technical quotes and won't break cases when the user simply used a backslash
     * as the last symbol (single quote) will be removed.
     */
    private fun String.convertSingleQuotes(): String = this.replace("\\'", "'")
}

/**
 * Toml AST Node for a representation of string values: key = "value" (always should have quotes due to TOML standard)
 */
public class TomlBasicString(content: String, lineNo: Int) : TomlValue(lineNo) {
    override var content: Any = if (content.startsWith("\"") && content.endsWith("\"")) {
        content.trimQuotes()
            .checkOtherQuotesAreEscaped()
            .convertSpecialCharacters()
    } else {
        throw TomlParsingException(
            "According to the TOML specification string values (even Enums)" +
                    " should be wrapped (start and end) with quotes (\"\"), but the following value was not: <$content>." +
                    " Please note that multiline strings are not yet supported.",
            lineNo
        )
    }

    private fun String.checkOtherQuotesAreEscaped(): String {
        this.forEachIndexed { index, ch ->
            if (ch == '\"' && (index == 0 || this[index - 1] != '\\')) {
                throw TomlParsingException(
                    "Found invalid quote that is not escaped." +
                            " Please remove the quote or use escaping" +
                            " in <$this> at position = [$index].", lineNo
                )
            }
        }
        return this
    }

    private fun String.convertSpecialCharacters(): String {
        val resultString = StringBuilder()
        var updatedOnPreviousStep = false
        var i = 0
        while (i < this.length) {
            val newCharacter = if (this[i] == '\\' && i != this.length - 1) {
                updatedOnPreviousStep = true
                when (this[i + 1]) {
                    // table that is used to convert escaped string literals to proper char symbols
                    't' -> '\t'
                    'b' -> '\b'
                    'r' -> '\r'
                    'n' -> '\n'
                    '\\' -> '\\'
                    '\'' -> '\''
                    '"' -> '"'
                    else -> throw TomlParsingException(
                        "According to TOML documentation unknown" +
                                " escape symbols are not allowed. Please check: [\\${this[i + 1]}]",
                        lineNo
                    )
                }
            } else {
                this[i]
            }
            // need to skip the next character if we have processed special escaped symbol
            if (updatedOnPreviousStep) {
                updatedOnPreviousStep = false
                i += 2
            } else {
                i += 1
            }

            resultString.append(newCharacter)
        }
        return resultString.toString()
    }
}

/**
 * Toml AST Node for a representation of Arbitrary 64-bit signed integers: key = 1
 */
public class TomlLong(content: String, lineNo: Int) : TomlValue(lineNo) {
    override var content: Any = content.toLong()
}

/**
 * Toml AST Node for a representation of float types: key = 1.01.
 * Toml specification requires floating point numbers to be IEEE 754 binary64 values,
 * so it should be Kotlin Double (64 bits)
 */
public class TomlDouble(content: String, lineNo: Int) : TomlValue(lineNo) {
    override var content: Any = content.toDouble()
}

/**
 * Toml AST Node for a representation of boolean types: key = true | false
 */
public class TomlBoolean(content: String, lineNo: Int) : TomlValue(lineNo) {
    override var content: Any = content.toBoolean()
}

/**
 * Toml AST Node for a representation of null:
 * null, nil, NULL, NIL or empty (key = )
 */
public class TomlNull(lineNo: Int) : TomlValue(lineNo) {
    override var content: Any = "null"
}

/**
 * Toml AST Node for a representation of arrays: key = [value1, value2, value3]
 */
public class TomlArray(
    private val rawContent: String,
    lineNo: Int,
    tomlConfig: TomlConfig = TomlConfig()
) : TomlValue(lineNo) {
    override lateinit var content: Any

    init {
        validateBrackets()
        this.content = parse()
    }

    /**
     * small adaptor to make proper testing of parsing
     *
     * @param tomlConfig
     * @return converted array to a list
     */
    public fun parse(tomlConfig: TomlConfig = TomlConfig()): List<Any> = rawContent.parse(tomlConfig)

    /**
     * recursively parse TOML array from the string: [ParsingArray -> Trimming values -> Parsing Nested Arrays]
     */
    private fun String.parse(tomlConfig: TomlConfig = TomlConfig()): List<Any> =
            this.parseArray()
                .map { it.trim() }
                .map { if (it.startsWith("[")) it.parse(tomlConfig) else it.parseValue(lineNo, tomlConfig) }

    /**
     * method for splitting the string to the array: "[[a, b], [c], [d]]" to -> [a,b] [c] [d]
     */
    @Suppress("TOO_MANY_LINES_IN_LAMBDA")
    private fun String.parseArray(): MutableList<String> {
        // covering cases when the array is intentionally blank: myArray = []. It should be empty and not contain null
        if (this.trimBrackets().isBlank()) {
            return mutableListOf()
        }

        var numberOfOpenBrackets = 0
        var numberOfClosedBrackets = 0
        var bufferBetweenCommas = StringBuilder()
        val result: MutableList<String> = mutableListOf()

        this.trimBrackets().forEach {
            when (it) {
                '[' -> {
                    numberOfOpenBrackets++
                    bufferBetweenCommas.append(it)
                }
                ']' -> {
                    numberOfClosedBrackets++
                    bufferBetweenCommas.append(it)
                }
                // split only if we are on the highest level of brackets (all brackets are closed)
                ',' -> if (numberOfClosedBrackets == numberOfOpenBrackets) {
                    result.add(bufferBetweenCommas.toString())
                    bufferBetweenCommas = StringBuilder()
                } else {
                    bufferBetweenCommas.append(it)
                }
                else -> bufferBetweenCommas.append(it)
            }
        }
        result.add(bufferBetweenCommas.toString())
        return result
    }

    /**
     * small validation for quotes: each quote should be closed in a key
     */
    private fun validateBrackets() {
        if (rawContent.count { it == '\"' } % 2 != 0 || rawContent.count { it == '\'' } % 2 != 0) {
            throw TomlParsingException(
                "Not able to parse the key: [$rawContent] as it does not have closing bracket",
                lineNo
            )
        }
    }
}
