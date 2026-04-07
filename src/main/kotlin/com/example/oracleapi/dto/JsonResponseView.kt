package com.example.oracleapi.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

class JsonResponseView<T>(
    @get:Schema(description = "Количество записей")
    val count: Int,

    @get:JsonProperty("execution_time_ms")
    @get:Schema(description = "Время выполнения в миллисекундах")
    val executionTimeMs: Long,

    @get:Schema(description = "Данные ответа")
    val row: List<T>,

    val message: String? = null  // добавляем опциональное сообщение
)