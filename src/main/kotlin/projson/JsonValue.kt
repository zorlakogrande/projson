package projson

/**
 * Classe base de todos os valores JSON.
 *
 * Representa qualquer valor possível em JSON.
 * Usa sealed class para garantir que apenas os subtipos
 * definidos existem, permitindo ao compilador verificar
 * que todos os casos são tratados.
 *
 * Subtipos disponíveis:
 * - [JsonPrimitive] → null, String, Number, Boolean
 * - [JsonArray] → listas
 * - [JsonObject] → objectos com propriedades
 * - [JsonReference] → referência a outro objecto via UUID
 *
 * Exemplo:
 * ```
 * val json: JsonValue = JsonPrimitive("olá")
 * println(json) // "olá"
 * ```
 */
sealed class JsonValue {

    /**
     * Converte este valor JSON para texto formatado.
     *
     * @param indent nível de indentação actual (default: 0)
     * @return string com o JSON formatado
     */
    abstract fun toJsonString(indent: Int = 0): String

    /**
     * Devolve a representação textual do valor JSON.
     * Chama [toJsonString] com indentação zero.
     */
    override fun toString() = toJsonString()
}
