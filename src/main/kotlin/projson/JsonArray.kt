package projson

/**
 * Representa uma lista JSON — [ ... ]
 *
 * Pode conter qualquer tipo de [JsonValue]:
 * primitivos, objectos, outras listas, ou referências.
 *
 * Exemplo:
 * ```
 * val array = JsonArray()
 * array.add("a")
 * array.add(42)
 * array.add(null)
 * println(array) // ["a", 42, null]
 * ```
 *
 * @param elements lista interna de elementos (default: vazia)
 */
class JsonArray(
    private val elements: MutableList<JsonValue> = mutableListOf()
) : JsonValue() {

    /**
     * Adiciona um elemento à lista.
     * Aceita qualquer tipo Kotlin — converte automaticamente para [JsonValue].
     *
     * @param value valor a adicionar — String, Number, Boolean, null, ou JsonValue
     * @throws IllegalArgumentException se o tipo não for suportado
     */
    fun add(value: Any?) {
        elements.add(toJsonValue(value))
    }

    /**
     * Devolve o elemento na posição indicada.
     *
     * @param index posição do elemento (começa em 0)
     * @return o [JsonValue] nessa posição
     */
    fun get(index: Int): JsonValue = elements[index]

    /**
     * Remove o elemento na posição indicada.
     *
     * @param index posição do elemento a remover (começa em 0)
     */
    fun remove(index: Int) {
        elements.removeAt(index)
    }

    /**
     * Devolve o número de elementos na lista.
     *
     * @return número de elementos
     */
    fun size(): Int = elements.size

    /**
     * Percorre todos os elementos da lista.
     *
     * @param action função a executar para cada elemento
     */
    fun forEach(action: (JsonValue) -> Unit) {
        elements.forEach(action)
    }

    /**
     * Converte a lista para texto JSON formatado.
     * Se todos os elementos forem primitivos, fica numa linha.
     * Caso contrário, cada elemento fica na sua própria linha.
     *
     * @param indent nível de indentação actual
     * @return string com o JSON formatado
     */
    override fun toJsonString(indent: Int): String {
        if (elements.isEmpty()) return "[]"

        val tab = "\t".repeat(indent)
        val innerTab = "\t".repeat(indent + 1)

        val allPrimitive = elements.all { it is JsonPrimitive }
        return if (allPrimitive) {
            "[${elements.joinToString(", ") { it.toJsonString() }}]"
        } else {
            val content = elements.joinToString(",\n") {
                "$innerTab${it.toJsonString(indent + 1)}"
            }
            "[\n$content\n$tab]"
        }
    }
}

/**
 * Converte tipos Kotlin comuns para [JsonValue].
 * Função auxiliar interna — não faz parte da API pública.
 *
 * @param value valor a converter
 * @return o [JsonValue] correspondente
 * @throws IllegalArgumentException se o tipo não for suportado
 */
private fun toJsonValue(value: Any?): JsonValue = when (value) {
    null         -> JsonPrimitive(null)
    is JsonValue -> value
    is String    -> JsonPrimitive(value)
    is Number    -> JsonPrimitive(value)
    is Boolean   -> JsonPrimitive(value)
    else -> throw IllegalArgumentException(
        "Não sei converter ${value.javaClass} para JsonValue"
    )
}