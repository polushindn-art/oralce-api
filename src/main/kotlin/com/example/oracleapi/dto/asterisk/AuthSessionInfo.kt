package com.example.oracleapi.dto.asterisk

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/**
 * Информация о сессии для мониторинга
 */
@Schema(description = "Информация о сессии авторизации")
data class AuthSessionInfo(
    @field:Schema(description = "ID сессии")
    val actionId: String,

    @field:Schema(description = "Номер телефона")
    val phoneNumber: String,

    @field:Schema(description = "Логин пользователя")
    val userLogin: String? = null,

    @field:Schema(description = "Статус сессии")
    val status: String,

    @field:Schema(description = "Время создания")
    val createdAt: LocalDateTime?,

    @field:Schema(description = "Время истечения")
    val expiresAt: LocalDateTime?,

    @field:Schema(description = "Длительность в секундах")
    val duration: Long? = null
)