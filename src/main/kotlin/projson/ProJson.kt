package projson

import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.findAnnotation
import java.util.IdentityHashMap

/**
 * Motor principal da biblioteca ProJson.
 *
 * Converte qualquer objecto Kotlin para o modelo JSON em memória,
 * gerindo automaticamente referências entre objectos via UUIDs.
 *
 * Cada instância de [ProJson] mantém a sua própria memória de
 * objectos serializados — usa instâncias separadas para contextos
 * de serialização independentes.
 *
 * Exemplo básico:
 * ```
 * val json = ProJson().toJson("olá")
 * println(json) // "olá"
 * ```
 *
 * Exemplo com referências:
 * ```
 * class Task(
 *     val description: String,
 *     @Reference
 *     val dependencies: List<Task>
 * )
 *
 * val t1 = Task("T1", emptyList())
 * val t2 = Task("T2", listOf(t1))
 * val json = ProJson().toJson(listOf(t1, t2))
 * println(json)
 * ```
 */
class ProJson {

    /**
     * Memória de objectos já serializados.
     * Associa cada objecto ao seu UUID para evitar duplicação.
     * Usa [IdentityHashMap] para comparar por identidade real
     * e não por valor.
     */
    private val objectIds = IdentityHashMap<Any, String>()

    /**
     * Converte qualquer objecto Kotlin para [JsonValue].
     *
     * Regras de conversão:
     * - null → [JsonPrimitive]
     * - String, Number, Boolean → [JsonPrimitive]
     * - Collection → [JsonArray] (recursivo)
     * - Map → [JsonObject] sem $type (recursivo)
     * - Qualquer classe → [JsonObject] com $type (reflexão)
     *
     * @param obj objecto a converter — pode ser null
     * @return o [JsonValue] correspondente
     */
    fun toJson(obj: Any?): JsonValue = when {
        obj == null          -> JsonPrimitive(null)
        obj is String        -> JsonPrimitive(obj)
        obj is Number        -> JsonPrimitive(obj)
        obj is Boolean       -> JsonPrimitive(obj)
        obj is Collection<*> -> collectionToJson(obj)
        obj is Map<*, *>     -> mapToJson(obj)
        else                 -> objectToJson(obj)
    }

    /**
     * Converte uma [Collection] para [JsonArray].
     * Chama [toJson] recursivamente para cada elemento.
     *
     * @param col a colecção a converter
     * @return [JsonArray] com todos os elementos convertidos
     */
    private fun collectionToJson(col: Collection<*>): JsonArray {
        val array = JsonArray()
        col.forEach { array.add(toJson(it)) }
        return array
    }

    /**
     * Converte um [Map] para [JsonObject] sem $type.
     * Chama [toJson] recursivamente para cada valor.
     *
     * @param map o mapa a converter
     * @return [JsonObject] com todas as entradas convertidas
     */
    private fun mapToJson(map: Map<*, *>): JsonObject {
        val obj = JsonObject()
        map.forEach { (k, v) ->
            obj.setProperty(k.toString(), toJson(v))
        }
        return obj
    }

    /**
     * Converte qualquer objecto para [JsonObject] usando reflexão.
     *
     * Comportamento:
     * - Adiciona $type com o nome da classe
     * - Adiciona $id com UUID único (apenas para classes normais)
     * - Respeita as anotações [@JsonIgnore], [@JsonProperty], [@Reference]
     * - Suporta serialização personalizada via [@JsonString]
     * - Chama [toJson] recursivamente para cada propriedade
     *
     * @param obj o objecto a converter — não pode ser null
     * @return [JsonObject] com todas as propriedades convertidas
     */
    private fun objectToJson(obj: Any): JsonObject {
        val kClass = obj::class

        // Verifica se a classe tem @JsonString
        val jsonString = kClass.findAnnotation<JsonString>()
        if (jsonString != null) {
            val converter = jsonString.converter.objectInstance
                ?: jsonString.converter.constructors.first().call()
            return JsonObject().also {
                it.setProperty("value", converter.convert(obj))
            }
        }

        val jsonObj = JsonObject()

        // Só data class não tem $id — representa valores, não objectos
        if (!kClass.isData) {
            val uuid = objectIds.getOrPut(obj) {
                java.util.UUID.randomUUID().toString()
            }
            jsonObj.setProperty("\$id", uuid)
        }

        jsonObj.setProperty("\$type", kClass.simpleName ?: "Unknown")

        // Reflexão — processa cada propriedade
        kClass.memberProperties.forEach { prop ->

            // @JsonIgnore → salta esta propriedade
            if (prop.hasAnnotation<JsonIgnore>()) return@forEach

            // @JsonProperty → usa o nome personalizado
            val jsonProp = prop.findAnnotation<JsonProperty>()
            val nome = jsonProp?.name ?: prop.name

            val valor = prop.call(obj)

            // @Reference → usa JsonReference em vez de serializar
            if (prop.hasAnnotation<Reference>()) {
                if (valor is Collection<*>) {
                    val array = JsonArray()
                    valor.forEach { item ->
                        if (item != null) {
                            val itemUuid = objectIds.getOrPut(item) {
                                java.util.UUID.randomUUID().toString()
                            }
                            array.add(JsonReference(itemUuid))
                        }
                    }
                    jsonObj.setProperty(nome, array)
                } else if (valor != null) {
                    val refUuid = objectIds.getOrPut(valor) {
                        java.util.UUID.randomUUID().toString()
                    }
                    jsonObj.setProperty(nome, JsonReference(refUuid))
                } else {
                    jsonObj.setProperty(nome, JsonPrimitive(null))
                }
                return@forEach
            }

            // Caso normal → serializa recursivamente
            jsonObj.setProperty(nome, toJson(valor))
        }

        return jsonObj
    }
}

