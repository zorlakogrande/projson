package projson

/**
 * Representa um valor primitivo JSON.
 *
 * Tipos suportados:
 * - String → aparece com aspas no JSON: "olá"
 * - Number → aparece sem aspas: 42, 3.14
 * - Boolean → aparece como true ou false
 * - null → aparece como null
 *
 * Exemplo:
 * ```
 * val s = JsonPrimitive("olá")  // "olá"
 * val n = JsonPrimitive(42)     // 42
 * val b = JsonPrimitive(true)   // true
 * val x = JsonPrimitive(null)   // null
 * ```
 *
 * @param value o valor a representar — deve ser String, Number, Boolean ou null
 * @throws IllegalArgumentException se o tipo não for suportado
 */
class JsonPrimitive(val value: Any?) : JsonValue() {

    init {
        require(
            value == null ||
                    value is String ||
                    value is Number ||
                    value is Boolean
        ) { "Tipo inválido para JsonPrimitive: ${value?.javaClass}" }
    }

    /**
     * Converte o valor para texto JSON.
     * Strings aparecem com aspas, os restantes sem aspas.
     *
     * @param indent ignorado — primitivos não têm indentação
     * @return representação textual do valor
     */
    override fun toJsonString(indent: Int): String = when (value) {
        null      -> "null"
        is String -> "\"$value\""
        else      -> value.toString()
    }
}

