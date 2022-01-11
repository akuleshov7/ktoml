package com.akuleshov7.ktoml

/**
 * @property ignoreUnknownNames - a control to allow/prohibit unknown names during the deserialization
 * @property emptyValuesAllowed - a control to allow/prohibit empty values: a = # comment
 * @property escapedQuotesInLiteralStringsAllowed - a control to allow/prohibit escaping of single quotes in literal strings
 */
public data class TomlConfig(
    val ignoreUnknownNames: Boolean = false,
    val emptyValuesAllowed: Boolean = true,
    val escapedQuotesInLiteralStringsAllowed: Boolean = true
)
