# ProJson

JSON Generation Library with References

## O que é?

ProJson é uma biblioteca Kotlin que converte automaticamente
objectos em memória para JSON, com suporte a referências entre
objectos via UUIDs.

## Instalação

Adiciona o JAR ao teu projecto:

```kotlin
dependencies {
    implementation(files("projson-1.0.jar"))
}
```

## Uso básico

### Primitivos
```kotlin
val proJson = ProJson()
println(proJson.toJson("olá"))   // "olá"
println(proJson.toJson(42))      // 42
println(proJson.toJson(null))    // null
```

### Objectos
```kotlin
data class Date(val day: Int, val month: Int, val year: Int)

val date = Date(30, 2, 2026)
println(ProJson().toJson(date))
// {
//     "$type": "Date",
//     "day": 30,
//     "month": 2,
//     "year": 2026
// }
```

### Listas
```kotlin
val list = listOf("a", "b", "c")
println(ProJson().toJson(list))  // ["a", "b", "c"]
```

### Referências entre objectos
```kotlin
class Task(
    val description: String,
    val deadline: Date?,
    @Reference
    val dependencies: List<Task>
)

val t1 = Task("T1", Date(30, 2, 2026), emptyList())
val t2 = Task("T2", Date(31, 4, 2026), emptyList())
val t3 = Task("T3", null, listOf(t1, t2))

println(ProJson().toJson(listOf(t1, t2, t3)))
// T3 aponta para T1 e T2 via $ref em vez de duplicar
```

## Anotações disponíveis

| Anotação | Descrição | Exemplo |
|---|---|---|
| `@Reference` | Usa referência UUID em vez de duplicar | `@Reference val deps: List<Task>` |
| `@JsonIgnore` | Ignora a propriedade no JSON | `@JsonIgnore val password: String` |
| `@JsonProperty` | Muda o nome da propriedade no JSON | `@JsonProperty("desc") val description: String` |
| `@JsonString` | Serializa o objecto como string personalizada | `@JsonString(DateAsText::class) data class Date(...)` |

## Manipulação do JSON

```kotlin
val json = ProJson().toJson(Date(30, 2, 2026)) as JsonObject

// Ler
json.getProperty("day")        // JsonPrimitive(30)

// Alterar
json.setProperty("year", 2027)

// Remover
json.removeProperty("month")

// Percorrer
json.forEach { chave, valor ->
    println("$chave = $valor")
}
```

## Licença

MIT