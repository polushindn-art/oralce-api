package com.example.oracleapi.dto.asterisk

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

/**
 * Запрос на инициацию звонка для авторизации
 */
@Schema(description = "Запрос на инициацию звонка для авторизации")
data class AuthCallRequest(
    @field:NotBlank(message = "Номер телефона обязателен")
    @field:Pattern(
        regexp = "^\\+?[0-9]{10,15}$",
        message = "Неверный формат номера телефона"
    )
    @field:Schema(description = "Номер телефона для авторизации", example = "+79635328259")
    val phoneNumber: String,

    @field:Schema(description = "Caller ID (отображаемый номер)", example = "73852361105")
    val callerId: String? = null,

    @field:Schema(description = "Префикс набора номера", example = "800")
    val prefix: String? = null,
)