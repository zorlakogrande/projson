package projson

import kotlin.reflect.KClass

/**
 * Indica que uma propriedade deve ser serializada como referência.
 *
 * Em vez de duplicar o objecto no JSON, usa um [JsonReference]
 * com o UUID do objecto já serializado.
 *
 * Exemplo:
 * ```
 * class Task(
 *     val description: String,
 *     @Reference
 *     val dependencies: List<Task>
 * )
 * ```
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Reference

/**
 * Permite personalizar o nome de uma propriedade no JSON.
 *
 * Por defeito, o nome da propriedade no JSON é igual ao nome
 * da variável em Kotlin. Com esta anotação podes mudá-lo.
 *
 * Exemplo:
 * ```
 * class Task(
 *     @JsonProperty("desc")
 *     val description: String
 * )
 * // resultado: { "desc": "T1" }
 * ```
 *
 * @param name nome a usar no JSON
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonProperty(val name: String)

/**
 * Indica que uma propriedade deve ser ignorada na serialização.
 *
 * A propriedade não aparece no JSON gerado.
 * Útil para campos sensíveis como passwords.
 *
 * Exemplo:
 * ```
 * class User(
 *     val nome: String,
 *     @JsonIgnore
 *     val password: String
 * )
 * // resultado: { "nome": "João" }
 * ```
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonIgnore

/**
 * Indica que os objectos desta classe devem ser serializados
 * como strings personalizadas em vez de objectos JSON.
 *
 * Requer uma classe conversora que implemente [JsonStringConverter].
 *
 * Exemplo:
 * ```
 * class DateAsText : JsonStringConverter {
 *     override fun convert(obj: Any): String {
 *         obj as Date
 *         return "${obj.day}/${obj.month}/${obj.year}"
 *     }
 * }
 *
 * @JsonString(DateAsText::class)
 * data class Date(val day: Int, val month: Int, val year: Int)
 * // resultado: "30/02/2026"
 * ```
 *
 * @param converter classe que implementa a conversão para string
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonString(val converter: KClass<out JsonStringConverter>)

/**
 * Interface que as classes conversoras devem implementar
 * para usar com a anotação [@JsonString].
 *
 * Exemplo:
 * ```
 * class DateAsText : JsonStringConverter {
 *     override fun convert(obj: Any): String {
 *         obj as Date
 *         return "${obj.day}/${obj.month}/${obj.year}"
 *     }
 * }
 * ```
 */
interface JsonStringConverter {
    /**
     * Converte um objecto para a sua representação em string.
     *
     * @param obj o objecto a converter
     * @return string com a representação personalizada
     */
    fun convert(obj: Any): String
}
