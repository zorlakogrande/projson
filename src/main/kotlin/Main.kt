package projson

data class Date(val day: Int, val month: Int, val year: Int)

class Task(
    val description: String,
    val deadline: Date?,
    @Reference
    val dependencies: List<Task>
)

class TaskComAnotacoes(
    @JsonProperty("desc")
    val description: String,
    @JsonIgnore
    val password: String,
    @JsonProperty("deps")
    @Reference
    val dependencies: List<Task>
)

class DateAsText : JsonStringConverter {
    override fun convert(obj: Any): String {
        obj as DateCustom
        return "${obj.day}/${obj.month}/${obj.year}"
    }
}

@JsonString(DateAsText::class)
data class DateCustom(val day: Int, val month: Int, val year: Int)

fun demo(titulo: String, obj: Any?) {
    println("\n========== $titulo ==========")
    println(ProJson().toJson(obj))
}

fun main() {
    // Fase 1
    demo("Primitivo String", "olá")
    demo("Primitivo Número", 42)
    demo("Primitivo Null", null)
    demo("Array", listOf("a", "b", "c"))
    demo("Date (objecto simples)", Date(30, 2, 2026))
    demo("Mapa", mapOf("nome" to "João", "idade" to 25))

    // Fase 2
    val t1 = Task("T1", Date(30, 2, 2026), emptyList())
    val t2 = Task("T2", Date(31, 4, 2026), emptyList())
    val t3 = Task("T3", null, listOf(t1, t2))
    demo("References (UUIDs)", listOf(t1, t2, t3))

    demo("@JsonIgnore e @JsonProperty",
        TaskComAnotacoes("T1", "password123", emptyList()))

    demo("@JsonString",
        listOf(DateCustom(30, 2, 2026), DateCustom(31, 4, 2026)))
}