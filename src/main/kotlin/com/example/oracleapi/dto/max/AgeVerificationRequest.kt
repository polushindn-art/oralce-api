package com.example.oracleapi.dto.max

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

@Schema(description = "Запрос на проверку возраста через Цифровой ID MAX")
data class AgeVerificationRequest(

    @field:NotBlank(message = "sessionId не может быть пустым")
    @field:Pattern(
        regexp = "^https://www\\.gosuslugi\\.ru/m.*$",
        message = "sessionId должен начинаться с https://www.gosuslugi.ru/m"
    )
    @field:Schema(
        description = "Session ID из QR-кода MAX",
        example = "https://www.gosuslugi.ru/m5/kjkjJHgJHgk?p=f",
        required = true
    )
    val sessionId: String
)