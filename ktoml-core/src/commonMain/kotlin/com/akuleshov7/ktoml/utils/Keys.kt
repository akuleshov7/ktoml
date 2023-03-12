/**
 * Contains functions to determine the type of key during encoding and writing.
 */

package com.akuleshov7.ktoml.utils

/**
 * Returns true if the specified key is a bare key, a key containing only
 * alphanumeric, underscore, and hyphen characters.
 *
 * @return `true` if this key is a bare key.
 */
internal fun String.isBareKey() =
        all {
            it in 'A'..'Z' ||
                    it in 'a'..'z' ||
                    it in '0'..'9' ||
                    it in "-_"
        }

/**
 * Returns true if the specified key contains at least one unescaped double
 * quote and no single quotes.
 *
 * @return `true` if this key can be written as a literal key.
 */
internal fun String.isLiteralKeyCandidate(): Boolean {
    for ((i, char) in withIndex()) {
        if (char == '\'') {
            return false
        } else if (char == '"' && (i == 0 || this[i - 1] != '\\')) {
            return true
        }
    }

    return false
}
