package com.example.oracleapi.dto.userlist

import com.example.oracleapi.dto.tsdlist.Registeredjson
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

class RegisteredJsonResponse(
    // 1. ДАННЫЕ: список сессий
    @param:Schema(description = "Массив активных сессий")
    val sessions: List<Registeredjson>,

    // 2. МЕТАДАННЫЕ: количество записей
    @param:Schema(description = "Количество записей")
    val count: Int,

    // 3. МЕТАДАННЫЕ: время выполнения запроса
    @param:JsonProperty("execution_time_ms")
    @param:Schema(description = "Время выполнения в миллисекундах")
    val executionTimeMs: Long,

    // 4. МЕТАДАННЫЕ: временная метка ответа
    @param:Schema(description = "Временная метка")
    val timestamp: String
)