package com.example.oracleapi.dto.public

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GenIdResponse(
    val rn: Long? = null,                          // Основной идентификатор с понятным именем
    val rns: List<Long>? = null,            // Для множественной генерации
    val count: Int? = null,                      // Количество сгенерированных RN
    val timestamp: String                     // Временная метка
) {
    companion object {
        fun single(
            rn: Long
        ): GenIdResponse {
            return GenIdResponse(
                rn = rn,
                timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
        }

        fun multiple(
            ids: List<Long>
        ): GenIdResponse {
            return GenIdResponse(
                rns = ids,
                count = ids.size,
                timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
        }
    }
}