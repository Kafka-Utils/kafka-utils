package br.com.kafkautils.factories

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.annotation.Factory
import io.micronaut.core.convert.TypeConverter
import java.util.Optional
import javax.inject.Singleton
import kotlin.collections.HashSet

@Factory
class ConvertersFactory(
    private val objectMapper: ObjectMapper
) {

    @Singleton
    fun stringToSetOfStringsTypeConverter(): TypeConverter<String, Set<String>> {
        return TypeConverter { text, _, _ ->
            val collectionType = objectMapper.typeFactory.constructCollectionType(Set::class.java, String::class.java)
            val value = objectMapper.readValue<Set<String>>(text, collectionType)
            Optional.of(value)
        }
    }

    @Singleton
    fun setOfStringsToStringTypeConverter(): TypeConverter<HashSet<String>, String> {
        return TypeConverter { set, _, _ ->
            Optional.of(objectMapper.writeValueAsString(set))
        }
    }
}
