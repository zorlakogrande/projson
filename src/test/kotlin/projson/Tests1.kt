package projson

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

// Classes de teste — definidas fora para simular uso real
data class Date(val day: Int, val month: Int, val year: Int)

class Task(
    val description: String,
    val deadline: Date?,
    val dependencies: List<Task>
)

class Tests1 {

    val proJson = ProJson()

    // ==================
    // PRIMITIVOS
    // ==================

    @Test
    fun testPrimitiveString() {
        assertEquals("\"olá\"", proJson.toJson("olá").toString())
    }

    @Test
    fun testPrimitiveInt() {
        assertEquals("42", proJson.toJson(42).toString())
    }

    @Test
    fun testPrimitiveDouble() {
        assertEquals("3.14", proJson.toJson(3.14).toString())
    }

    @Test
    fun testPrimitiveBoolean() {
        assertEquals("true", proJson.toJson(true).toString())
    }

    @Test
    fun testPrimitiveNull() {
        assertEquals("null", proJson.toJson(null).toString())
    }

    @Test
    fun testPrimitiveInvalid() {
        // Garantir que tipos inválidos dão erro
        assertFailsWith<IllegalArgumentException> {
            JsonPrimitive(listOf(1, 2, 3))
        }
    }

    // ==================
    // ARRAYS
    // ==================

    @Test
    fun testEmptyArray() {
        assertEquals("[]", proJson.toJson(emptyList<Any>()).toString())
    }

    @Test
    fun testArrayPrimitives() {
        assertEquals("[\"a\", \"b\", \"c\"]", proJson.toJson(listOf("a", "b", "c")).toString())
    }

    @Test
    fun testArrayWithNull() {
        assertEquals("[\"a\", null, \"b\"]", proJson.toJson(listOf("a", null, "b")).toString())
    }

    @Test
    fun testArrayAdd() {
        val json = proJson.toJson(listOf("a", "b")) as JsonArray
        json.add("c")
        assertEquals("[\"a\", \"b\", \"c\"]", json.toString())
    }

    @Test
    fun testArrayRemove() {
        val json = proJson.toJson(listOf("a", "b", "c")) as JsonArray
        json.remove(1)
        assertEquals("[\"a\", \"c\"]", json.toString())
    }

    @Test
    fun testArraySize() {
        val json = proJson.toJson(listOf("a", "b", "c")) as JsonArray
        assertEquals(3, json.size())
    }

    // ==================
    // OBJECTOS
    // ==================

    @Test
    fun testObjectHasType() {
        val json = proJson.toJson(Date(30, 2, 2026)) as JsonObject
        assertEquals("\"Date\"", json.getProperty("\$type").toString())
    }

    @Test
    fun testObjectProperties() {
        val json = proJson.toJson(Date(30, 2, 2026)) as JsonObject
        assertEquals("30", json.getProperty("day").toString())
        assertEquals("2", json.getProperty("month").toString())
        assertEquals("2026", json.getProperty("year").toString())
    }

    @Test
    fun testObjectSetProperty() {
        val json = proJson.toJson(Date(30, 2, 2026)) as JsonObject
        json.setProperty("year", 2027)
        assertEquals("2027", json.getProperty("year").toString())
    }

    @Test
    fun testObjectRemoveProperty() {
        val json = proJson.toJson(Date(30, 2, 2026)) as JsonObject
        json.removeProperty("day")
        assertEquals(null, json.getProperty("day"))
    }

    @Test
    fun testObjectNullProperty() {
        val t = Task("T1", null, emptyList())
        val json = proJson.toJson(t) as JsonObject
        assertEquals("null", json.getProperty("deadline").toString())
    }

    // ==================
    // OBJECTOS ANINHADOS
    // ==================

    @Test
    fun testNestedObject() {
        val t = Task("T1", Date(30, 2, 2026), emptyList())
        val json = proJson.toJson(t) as JsonObject

        // Verifica $type da Task
        assertEquals("\"Task\"", json.getProperty("\$type").toString())

        // Verifica que deadline é um JsonObject com $type Date
        val deadline = json.getProperty("deadline") as JsonObject
        assertEquals("\"Date\"", deadline.getProperty("\$type").toString())
        assertEquals("30", deadline.getProperty("day").toString())
    }

    @Test
    fun testArrayOfObjects() {
        val t1 = Task("T1", Date(30, 2, 2026), emptyList())
        val t2 = Task("T2", Date(31, 4, 2026), emptyList())
        val json = proJson.toJson(listOf(t1, t2)) as JsonArray

        assertEquals(2, json.size())

        val first = json.get(0) as JsonObject
        assertEquals("\"Task\"", first.getProperty("\$type").toString())
        assertEquals("\"T1\"", first.getProperty("description").toString())
    }

    // ==================
    // MAPAS
    // ==================

    @Test
    fun testMap() {
        val json = proJson.toJson(mapOf("nome" to "João", "idade" to 25)) as JsonObject
        // Mapas não têm $type
        assertEquals(null, json.getProperty("\$type"))
        assertEquals("\"João\"", json.getProperty("nome").toString())
        assertEquals("25", json.getProperty("idade").toString())
    }
}

