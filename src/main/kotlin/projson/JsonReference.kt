package projson

/**
 * Representa uma referência a outro objecto JSON.
 *
 * Em vez de duplicar um objecto que já foi serializado,
 * usa um UUID para apontar para ele.
 * Aparece no JSON como: { "$ref": "uuid" }
 *
 * Só é criado automaticamente pelo [ProJson] quando uma
 * propriedade tem a anotação [@Reference].
 *
 * Exemplo de output:
 * ```
 * { "$ref": "9e2e6c64-3236-45b7-8b8a-11271c69e4df" }
 * ```
 *
 * @param uuid identificador único do objecto referenciado
 */
class JsonReference(val uuid: String) : JsonValue() {

    /**
     * Converte a referência para texto JSON.
     *
     * @param indent ignorado — referências ficam sempre numa linha
     * @return string no formato { "$ref": "uuid" }
     */
    override fun toJsonString(indent: Int): String {
        return "{ \"\$ref\": \"$uuid\" }"
    }
}
