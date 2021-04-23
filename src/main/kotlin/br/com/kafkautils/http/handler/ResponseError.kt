package br.com.kafkautils.http.handler

import io.micronaut.core.annotation.Introspected

@Introspected
data class ResponseError(val message: String?)
