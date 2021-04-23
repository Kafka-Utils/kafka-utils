package br.com.kafkautils.http.handler

data class ValidationErrorList(val total: Int, val message: String, val errors: List<ValidationError> = listOf())
