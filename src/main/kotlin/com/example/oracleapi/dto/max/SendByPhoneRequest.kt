package com.example.oracleapi.dto.max

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class SendByPhoneRequest(
    @field:NotBlank(message = "Номер телефона обязателен")
    val phone: String,

    @field:NotBlank(message = "Текст сообщения обязателен")
    val text: String,

    @field:Pattern(
        regexp = "^(markdown|html|plain)?$",
        message = "должен быть: markdown, html или plain"
    )
    @field:NotBlank(message = "Либо задать format markdown, html или plain либо не передавать. По умолчанию markdown")
    val format: String = "markdown"
)