package com.example.oracleapi.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class JacksonConfig {
    @Bean
    @Primary
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().apply {
            // Регистрируем модуль для Java 8 Date/Time
            registerModule(JavaTimeModule())
            // Регистрируем модуль для Kotlin
            registerModule(KotlinModule.Builder().build())
            // Отключаем запись дат как timestamp (чтобы были в ISO формате)
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }
}