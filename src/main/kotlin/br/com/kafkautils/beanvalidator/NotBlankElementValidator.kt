package br.com.kafkautils.beanvalidator

import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import org.apache.commons.lang3.StringUtils

@Singleton
class NotBlankElementValidator : ConstraintValidator<NotBlankElement, Collection<String>> {

    override fun isValid(
        value: Collection<String>?,
        annotationMetadata: AnnotationValue<NotBlankElement>,
        context: ConstraintValidatorContext
    ): Boolean {
        if (value == null) {
            return true
        }
        return value.all {
            StringUtils.isNotBlank(it)
        }
    }
}