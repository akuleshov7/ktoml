package com.akuleshov7.ktoml.parsers

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.tree.nodes.TomlKeyValuePrimitive
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class SetLineNoTest {
    @Test
    fun checkingLineNumbers() {
        val string = """
            
            # comment 1
            
            [a] # comment 2
            # comment 3
             test = 1 # comment 4
             
             # ====
             
             [[a.b]] # comment 5 
                test = 1
                
             mls = '''
                  1
                  2
                  3
             '''
             
             mla = [
                 "a",
                 "b",
                 "c"
                 ]
        """.trimIndent()
        val parsedToml = Toml.tomlParser.parseString(string)

        parsedToml.prettyPrint(true)
        assertEquals(
            """
                 | - TomlFile (rootNode)[line:0]
                 |     - TomlTablePrimitive ([a])[line:4]
                 |         - TomlKeyValuePrimitive (test=1)[line:6]
                 |         - TomlArrayOfTables ([[a.b]])[line:10]
                 |             - TomlArrayOfTablesElement (technical_node)[line:10]
                 |                 - TomlKeyValuePrimitive (test=1)[line:11]
                 |                 - TomlKeyValuePrimitive (mls='''      1
                 |      2
                 |      3
                 | ''')[line:13]
                 |                 - TomlKeyValueArray (mla=[ "a", "b", "c" ])[line:18]
                 |
            """.trimMargin(),
            parsedToml.prettyStr(true)
        )
    }
}
