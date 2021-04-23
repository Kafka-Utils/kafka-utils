package br.com.kafkautils.http

import br.com.kafkautils.http.handler.ResponseError
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import br.com.kafkautils.http.handler.ValidationErrorList
import java.lang.annotation.Inherited

@Target(AnnotationTarget.FUNCTION)
@Retention
@Inherited
@ApiResponses(
    ApiResponse(responseCode = "401", description = "Not authenticated."),
    ApiResponse(responseCode = "403", description = "Access denied."),
    ApiResponse(responseCode = "404", description = "Resource not found."),
    ApiResponse(responseCode = "422", description = "Validation error.",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ValidationErrorList::class))]
    ),
    ApiResponse(responseCode = "500", description = "Internal server error.",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ResponseError::class))]
    )
)
annotation class DefaultErrorResponses()
