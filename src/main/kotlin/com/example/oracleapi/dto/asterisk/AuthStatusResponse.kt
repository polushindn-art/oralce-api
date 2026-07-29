package com.example.oracleapi.dto.asterisk

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/**
 * Ответ на проверку статуса авторизации
 */
@Schema(description = "Ответ на проверку статуса авторизации")
data class AuthStatusResponse(
    @Schema(description = "Успешна ли авторизация")
    val success: Boolean,

    @Schema(description = "Статус звонка",
        allowableValues = ["INITIATED", "RINGING", "ANSWERED", "NOANSWER", "BUSY", "FAILED", "TIMEOUT", "CANCELED", "NOT_FOUND"])
    val status: String,

    @Schema(description = "Сообщение")
    val message: String,

    @Schema(description = "Номер телефона")
    val phone: String? = null,

    @Schema(description = "Логин пользователя")
    val userLogin: String? = null,

    @Schema(description = "Длительность разговора в секундах")
    val duration: Long? = null,

    @Schema(description = "Время ответа")
    val answeredAt: LocalDateTime? = null
)