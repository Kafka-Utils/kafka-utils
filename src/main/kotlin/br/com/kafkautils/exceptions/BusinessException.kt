package br.com.kafkautils.exceptions

open class BusinessException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
