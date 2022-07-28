package com.example.jsondeserializationstudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue


/*data class MyJSONDataClass(val name: String, val age: Int, val favorites: List<String>, val address: MyJSONDataClass2)
data class MyJSONDataClass2(val city: String, val let: Double, val lon: Double)*/

@JsonDeserialize(using=ComplexJSONDataDeserializer::class)
data class ComplexJSONDataClass(val innerData : String, val data1: Int, val data2: String, val list: List<Int>)
data class ComplexJSONNestedClass(val inner_data: String, val inner_nested: ComplexJSONInnerNested)
data class ComplexJSONInnerNested(val data1: Int, val data2: String, val list: List<Int>)

class ComplexJSONDataDeserializer:StdDeserializer<ComplexJSONDataClass>(
    ComplexJSONDataClass::class.java
){
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): ComplexJSONDataClass {
        val node=p?.codec?.readTree<JsonNode>(p)

        val nestedNode=node?.get("nested")
        val innerDataValue=nestedNode?.get("inner_data")?.asText() //쓸 타입으로 형변환(as~)
        val innerNestedNode=nestedNode?.get("inner_nested")
        val innerNestedData1=innerNestedNode?.get("data1")?.asInt()
        val innerNestedData2=innerNestedNode?.get("data2")?.asText()

        val list=mutableListOf<Int>()
        innerNestedNode?.get("list")?.elements()?.forEach{
            list.add(it.asInt())
        }

        return ComplexJSONDataClass(
            innerDataValue!!,
            innerNestedData1!!,
            innerNestedData2!!,
            list!!
        )

    }

}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var mapper=jacksonObjectMapper()

        val jsonString="""
            {"data1":1234,
            "data2":"Hello",
            "list":[1,2,3]
            }
        """.trimIndent()

        val jsonString2="""
            {
                "nested":{
                    "data1":1234,
                    "data2":"Hello",
                    "list":[1, 2, 3]
                }
            }
        """.trimIndent()

        val personString="""
            {
                "name": "John",
                "age": 20,
                "favorites": ["study", "game"],
                "address": {
                    "city": "Seoul",
                    "let": 0.0,
                    "lon": 1.0
                }
            }
        """.trimIndent()

        val complexJsonString="""
            {
                "nested": {
                    "inner_data": "Hello from inner",
                    "inner_nested": {
                        "data1": 1234,
                        "data2": "Hello",
                        "list": [1, 2, 3]
                    }
                }
            }
        """.trimIndent()

        val d1=mapper?.readValue<ComplexJSONDataClass>(complexJsonString)
        Log.d("mytag", d1.toString())
    }
}