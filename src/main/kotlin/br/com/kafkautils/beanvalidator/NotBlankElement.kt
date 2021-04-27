package br.com.kafkautils.beanvalidator

import java.lang.annotation.Inherited
import javax.validation.Constraint
import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
@Retention
@Inherited
@Constraint(validatedBy = [NotBlankElementValidator::class])
annotation class NotBlankElement(
    val message: String = "{javax.validation.constraints.NotBlank.message}",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Any>> = []
)
