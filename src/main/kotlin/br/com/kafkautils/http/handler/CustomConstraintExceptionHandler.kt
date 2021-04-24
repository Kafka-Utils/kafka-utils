package br.com.kafkautils.http.handler

import io.micronaut.context.annotation.Replaces
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import io.micronaut.validation.exceptions.ConstraintExceptionHandler
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Produces
@Singleton
@Replaces(ConstraintExceptionHandler::class)
@Requires(classes = [ConstraintViolationException::class, ExceptionHandler::class])
class CustomConstraintExceptionHandler: ExceptionHandler<ConstraintViolationException, HttpResponse<ValidationErrorList>> {
    override fun handle(
        request: HttpRequest<*>,
        exception: ConstraintViolationException
    ): HttpResponse<ValidationErrorList> {
        val constraintViolations = exception.constraintViolations
        if (constraintViolations == null || constraintViolations.isEmpty()) {
            val validationErrorList = ValidationErrorList(
                message = exception.message ?: HttpStatus.BAD_REQUEST.reason,
                total = 0
            )
            return HttpResponse.badRequest(validationErrorList)
        } else {
            val validationErrors = constraintViolations.map { violation ->
                ValidationError(
                    message = violation.message,
                    path = violation.propertyPath.toString(),
                    field = violation.propertyPath.iterator().asSequence().last().toString(),
                    rejectedValue = violation.invalidValue?.toString() ?: "null",
                )
            }
            val validationErrorList = ValidationErrorList(
                message = exception.message ?: HttpStatus.BAD_REQUEST.reason,
                total = constraintViolations.size,
                errors = validationErrors.toList()
            )
            return HttpResponse.badRequest(validationErrorList)
        }
    }
}