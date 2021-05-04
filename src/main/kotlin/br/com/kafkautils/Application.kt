package br.com.kafkautils

import io.micronaut.runtime.Micronaut.build
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.security.SecuritySchemes

@OpenAPIDefinition(
    info = Info(
        title = "kafka-utils",
        version = "0.0"
    )
)
@SecuritySchemes(
    SecurityScheme(
        type = SecuritySchemeType.HTTP,
        name = "Authorization",
        bearerFormat = "JWT",
        scheme = "bearer"
    )
)
object Api

fun main(args: Array<String>) {
    build()
        .args(*args)
        .packages("br.com.kafkautils")
        .start()
}
