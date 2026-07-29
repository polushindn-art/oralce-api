package com.example.oracleapi.dto.asterisk

import io.swagger.v3.oas.annotations.media.Schema

/**
 * Ответ на инициацию звонка
 */
@Schema(description = "Ответ на инициацию звонка")
data class AuthCallResponse(
    @field:Schema(description = "ID сессии для отслеживания")
    val actionId: String,

    @field:Schema(description = "Номер телефона")
    val phoneNumber: String,

    @field:Schema(description = "Статус звонка")
    val status: String,

    @field:Schema(description = "Сообщение")
    val message: String,

    @field:Schema(description = "URL для проверки статуса")
    val checkUrl: String,

    @field:Schema(description = "Таймаут в секундах")
    val timeoutSeconds: Int? = null
)