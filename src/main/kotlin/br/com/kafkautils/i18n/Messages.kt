package br.com.kafkautils.i18n

import io.micronaut.context.MessageSource
import java.util.*
import javax.inject.Singleton

@Singleton
class Messages {

    private val resourceBundleMessageSource = io.micronaut.context.i18n.ResourceBundleMessageSource("i18n.messages")

    fun getMessage(key: String, variables: Map<String, Any> = mapOf()): String {
        val context = MessageSource.MessageContext.of(Locale.ENGLISH, variables)
        return resourceBundleMessageSource.getMessage(key, context).orElse("null")
    }
}