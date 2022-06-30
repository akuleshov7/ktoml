package com.akuleshov7.ktoml.encoders

import com.akuleshov7.ktoml.TomlInputConfig
import com.akuleshov7.ktoml.tree.*
import kotlinx.serialization.SerializationStrategy

/**
 * Encodes a TOML file or table.
 * @property root The root node to add elements to.
 */
public class TomlMainEncoder(
    private val root: TomlNode,
    startElementIndex: Int = -1,
    config: TomlInputConfig = TomlInputConfig()
) : TomlAbstractEncoder(startElementIndex, config) {
    override lateinit var currentKey: String

    override fun encodeValue(
        value: TomlValue,
        comments: List<String>,
        inlineComment: String
    ) {
        val key = TomlKey(currentKey, elementIndex)

        root.appendChild(
            if (value is TomlArray) {
                TomlKeyValueArray(
                    key,
                    value,
                    elementIndex,
                    comments,
                    inlineComment,
                    currentKey,
                    config
                )
            } else {
                TomlKeyValuePrimitive(
                    key,
                    value,
                    elementIndex,
                    comments,
                    inlineComment,
                    currentKey,
                    config
                )
            }
        )
    }

    override fun encodeTable(value: TomlTable) {
        root.insertTableToTree(value)
    }

    override fun <T> encodeTableLike(
        serializer: SerializationStrategy<T>,
        value: T,
        isInline: Boolean,
        comments: List<String>,
        inlineComment: String
    ) {
        if (isInline) {
            val enc = TomlInlineTableEncoder(currentKey, elementIndex, config)

            serializer.serialize(enc, value)

            root.appendChild(
                TomlInlineTable(
                    "",
                    elementIndex,
                    currentKey,
                    enc.keyValues,
                    comments,
                    inlineComment,
                    config
                )
            )
        } else {
            val root = TomlTablePrimitive("[$currentKey]", elementIndex, comments, inlineComment, config)

            val enc = TomlMainEncoder(root, elementIndex, config)

            serializer.serialize(enc, value)

            encodeTable(root)

            elementIndex = enc.elementIndex
        }
    }

    public companion object {
        /**
         * Encodes the specified [value] into a [TomlFile].
         *
         * @param serializer The user-defined or compiler-generated serializer for
         * type [T].
         * @param value The value to serialize.
         * @param config The input config, used for constructing nodes.
         * @return The encoded [TomlFile] node.
         */
        public fun <T> encode(
            serializer: SerializationStrategy<T>,
            value: T,
            config: TomlInputConfig = TomlInputConfig()
        ): TomlFile {
            val root = TomlFile(config)

            val encoder = TomlMainEncoder(root, config = config)

            encoder.encodeSerializableValue(serializer, value)

            return root
        }
    }
}