package projson

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

// Classes de teste com anotações
class TaskRef(
    val description: String,
    val deadline: Date?,
    @Reference
    val dependencies: List<TaskRef>
)

class TaskIgnore(
    @JsonIgnore
    val password: String,
    val description: String
)

class TaskProperty(
    @JsonProperty("desc")
    val description: String,
    @JsonProperty("deps")
    val dependencies: List<TaskRef>
)

class DateAsText : JsonStringConverter {
    override fun convert(obj: Any): String {
        obj as DateString
        return "${obj.day}/${obj.month}/${obj.year}"
    }
}

@JsonString(DateAsText::class)
data class DateString(val day: Int, val month: Int, val year: Int)

class Tests2 {

    val proJson = ProJson()

    // ==================
    // @Reference
    // ==================

    @Test
    fun testReferenceCreatesId() {
        val t1 = TaskRef("T1", null, emptyList())
        val json = proJson.toJson(t1) as JsonObject
        // objectos com @Reference têm $id
        assertNotNull(json.getProperty("\$id"))
    }

    @Test
    fun testReferenceUsesRef() {
        val t1 = TaskRef("T1", null, emptyList())
        val t2 = TaskRef("T2", null, emptyList())
        val t3 = TaskRef("T3", null, listOf(t1, t2))
        val all = proJson.toJson(listOf(t1, t2, t3)) as JsonArray

        // t3 é o terceiro elemento
        val jsonT3 = all.get(2) as JsonObject
        val deps = jsonT3.getProperty("dependencies") as JsonArray

        // as dependências devem ser referências
        val ref1 = deps.get(0) as JsonReference
        val ref2 = deps.get(1) as JsonReference

        // os UUIDs devem ser os mesmos de t1 e t2
        val jsonT1 = all.get(0) as JsonObject
        val jsonT2 = all.get(1) as JsonObject

        assertEquals(jsonT1.getProperty("\$id").toString().trim('"'), ref1.uuid)
        assertEquals(jsonT2.getProperty("\$id").toString().trim('"'), ref2.uuid)
    }

    // ==================
    // @JsonIgnore
    // ==================

    @Test
    fun testJsonIgnore() {
        val t = TaskIgnore("password123", "T1")
        val json = proJson.toJson(t) as JsonObject
        // password não deve aparecer
        assertEquals(null, json.getProperty("password"))
        // description deve aparecer
        assertNotNull(json.getProperty("description"))
    }

    // ==================
    // @JsonProperty
    // ==================

    @Test
    fun testJsonProperty() {
        val t = TaskProperty("T1", emptyList())
        val json = proJson.toJson(t) as JsonObject
        // deve aparecer como "desc" não "description"
        assertNotNull(json.getProperty("desc"))
        assertEquals(null, json.getProperty("description"))
    }

    // ==================
    // @JsonString
    // ==================

    @Test
    fun testJsonString() {
        val d = DateString(30, 2, 2026)
        val json = proJson.toJson(d) as JsonObject
        assertEquals("\"30/2/2026\"", json.getProperty("value").toString())
    }
}

