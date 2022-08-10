package com.akuleshov7.ktoml.encoders

import com.akuleshov7.ktoml.TomlInputConfig
import com.akuleshov7.ktoml.TomlOutputConfig
import com.akuleshov7.ktoml.exceptions.UnsupportedEncodingFeatureException
import com.akuleshov7.ktoml.tree.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeEncoder

/**
 * Encodes a TOML array or table array.
 */
@OptIn(ExperimentalSerializationApi::class)
public class TomlArrayEncoder internal constructor(
    private val rootNode: TomlNode,
    private val parent: TomlAbstractEncoder?,
    elementIndex: Int,
    attributes: Attributes,
    inputConfig: TomlInputConfig,
    outputConfig: TomlOutputConfig
) : TomlAbstractEncoder(
    elementIndex,
    attributes,
    inputConfig,
    outputConfig
) {
    private lateinit var values: MutableList<TomlValue>
    private lateinit var tables: TomlArrayOfTables

    /**
     * @param rootNode The root node to add the array to.
     * @param elementIndex The current element index.
     * @param attributes The current attributes.
     * @param inputConfig The input config, used for constructing nodes.
     * @param outputConfig The output config.
     */
    public constructor(
        rootNode: TomlNode,
        elementIndex: Int,
        attributes: Attributes,
        inputConfig: TomlInputConfig,
        outputConfig: TomlOutputConfig
    ) : this(
        rootNode,
        parent = null,
        elementIndex,
        attributes,
        inputConfig,
        outputConfig
    )

    override fun nextElementIndex(): Int {
        // All key-value array elements are on the same line; only increment for
        // table arrays.
        return if (!attributes.isInline) {
            super.nextElementIndex()
        } else {
            elementIndex
        }
    }

    override fun isNextElementKey(descriptor: SerialDescriptor, index: Int): Boolean = false

    override fun appendValue(value: TomlValue) {
        values += value

        super.appendValue(value)
    }

    override fun encodeStructure(kind: SerialKind): TomlAbstractEncoder = if (attributes.isInline) {
        if (kind == StructureKind.LIST) {
            // Nested primitive array
            TomlArrayEncoder(
                rootNode,
                parent = this,
                elementIndex,
                attributes,
                inputConfig,
                outputConfig
            )
        } else {
            throw UnsupportedEncodingFeatureException(
                "Inline tables are not yet supported as array elements."
            )
        }
    } else {
        val element = TomlArrayOfTablesElement(
            elementIndex,
            attributes.comments,
            attributes.inlineComment,
            inputConfig
        )

        tables.appendChild(element)

        TomlMainEncoder(
            element,
            nextElementIndex(),
            attributes,
            inputConfig,
            outputConfig
        )
    }

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder {
        if (attributes.isInline) {
            values = ArrayList(collectionSize)

            parent?.let {
                // Create an array nested in the specified parent list.
                appendValueTo(TomlArray(values, "", elementIndex), parent)
            } ?: run {
                val key = attributes.keyOrThrow()
                // Create a key-array pair and add it to the parent.
                val array = TomlKeyValueArray(
                    TomlKey(key, elementIndex),
                    TomlArray(values, "", elementIndex),
                    elementIndex,
                    attributes.comments,
                    attributes.inlineComment,
                    key,
                    inputConfig
                )
                rootNode.appendChild(array)
            }
        } else {
            tables = TomlArrayOfTables("[[${attributes.parent!!.getFullKey()}]]", elementIndex, inputConfig)

            rootNode.insertTableToTree(tables)
        }

        return super.beginStructure(descriptor)
    }
}