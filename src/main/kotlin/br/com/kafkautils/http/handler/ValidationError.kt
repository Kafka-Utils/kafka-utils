package br.com.kafkautils.http.handler

data class ValidationError(val message: String, val path: String, val field: String, val rejectedValue: String)
