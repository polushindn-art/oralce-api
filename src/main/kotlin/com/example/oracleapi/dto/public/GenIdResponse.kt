package com.example.oracleapi.dto.public

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GenIdResponse(
    val id: Long? = null,                          // Основной идентификатор с понятным именем
    val ids: List<Long>? = null,            // Для множественной генерации
    val count: Int? = null,                      // Количество сгенерированных ID
    val executionTimeMs: Long,               // Время выполнения в мс
    val timestamp: String                     // Временная метка
) {
    companion object {
        fun single(
            id: Long,
            executionTimeMs: Long
        ): GenIdResponse {
            return GenIdResponse(
                id = id,
                executionTimeMs = executionTimeMs,
                timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
        }

        fun multiple(
            ids: List<Long>,
            executionTimeMs: Long
        ): GenIdResponse {
            return GenIdResponse(
                ids = ids,
                count = ids.size,
                executionTimeMs = executionTimeMs,
                timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
        }
    }
}