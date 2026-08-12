package com.example.oracleapi.dto.sms

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class SmsSendMainBotLinkRequest(
    @field:NotBlank(message = "Номер телефона обязателен")
    @field:Pattern(
        regexp = "^\\+?\\d{10,15}$",
        message = "Номер телефона должен быть в формате +7XXXXXXXXXX или 79XXXXXXXXX"
    )
    val phone: String,

    val from: String? = null,  // имя отправителя

    val test: Boolean? = null,  // тестовый режим

    val ip: String? = null  // IP для защиты от флуда
)