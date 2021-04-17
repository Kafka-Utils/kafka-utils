package br.com.kafkautils

import io.micronaut.runtime.Micronaut.build
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info

@OpenAPIDefinition(
    info = Info(
        title = "kafka-utils",
        version = "0.0"
    )
)
object Api
// Test
fun main(args: Array<String>) {
    build()
        .args(*args)
        .packages("br.com.kafkautils")
        .start()
}
