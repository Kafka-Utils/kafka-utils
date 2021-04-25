package br.com.kafkautils.http.handler

import br.com.kafkautils.exceptions.BusinessException
import br.com.kafkautils.exceptions.ConflictException
import br.com.kafkautils.exceptions.ValidationException
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import javax.inject.Singleton

@Produces
@Singleton
@Requires(classes = [BusinessException::class, ExceptionHandler::class ])
class BusinessExceptionHandler : ExceptionHandler<BusinessException, HttpResponse<ResponseError>> {
    override fun handle(request: HttpRequest<*>, exception: BusinessException): HttpResponse<ResponseError> {
        return when (exception::class.java) {
            ValidationException::class.java -> HttpResponse.unprocessableEntity<ResponseError>()
                .body(ResponseError(message = exception.message))
            ConflictException::class.java -> HttpResponse.status<ResponseError>(HttpStatus.CONFLICT)
                .body(ResponseError(message = exception.message))
            else -> HttpResponse.serverError(ResponseError(message = exception.message))
        }
    }
}
