package projson

/**
 * Representa um objecto JSON — { "chave": valor, ... }
 *
 * Mantém a ordem de inserção das propriedades graças ao [LinkedHashMap].
 * Pode conter qualquer tipo de [JsonValue] como valor.
 *
 * Exemplo:
 * ```
 * val obj = JsonObject()
 * obj.setProperty("nome", "João")
 * obj.setProperty("idade", 25)
 * println(obj)
 * // {
 * //     "nome": "João",
 * //     "idade": 25
 * // }
 * ```
 *
 * @param properties mapa interno de propriedades (default: vazio)
 */
class JsonObject(
    private val properties: LinkedHashMap<String, JsonValue> = LinkedHashMap()
) : JsonValue() {

    /**
     * Devolve o valor de uma propriedade pelo nome.
     *
     * @param name nome da propriedade
     * @return o [JsonValue] associado, ou null se não existir
     */
    fun getProperty(name: String): JsonValue? = properties[name]

    /**
     * Define ou substitui uma propriedade.
     * Aceita qualquer tipo Kotlin — converte automaticamente para [JsonValue].
     *
     * @param name nome da propriedade
     * @param value valor a associar — String, Number, Boolean, null, ou JsonValue
     * @throws IllegalArgumentException se o tipo não for suportado
     */
    fun setProperty(name: String, value: Any?) {
        properties[name] = toJsonValue(value)
    }

    /**
     * Remove uma propriedade pelo nome.
     * Se a propriedade não existir, não faz nada.
     *
     * @param name nome da propriedade a remover
     */
    fun removeProperty(name: String) {
        properties.remove(name)
    }

    /**
     * Percorre todas as propriedades do objecto.
     *
     * @param action função a executar para cada par chave-valor
     */
    fun forEach(action: (String, JsonValue) -> Unit) {
        properties.forEach(action)
    }

    /**
     * Converte o objecto para texto JSON formatado.
     * Cada propriedade fica na sua própria linha com indentação.
     *
     * @param indent nível de indentação actual
     * @return string com o JSON formatado
     */
    override fun toJsonString(indent: Int): String {
        if (properties.isEmpty()) return "{}"

        val tab = "\t".repeat(indent)
        val innerTab = "\t".repeat(indent + 1)

        val content = properties.entries.joinToString(",\n") { (key, value) ->
            "$innerTab\"$key\": ${value.toJsonString(indent + 1)}"
        }
        return "{\n$content\n$tab}"
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

