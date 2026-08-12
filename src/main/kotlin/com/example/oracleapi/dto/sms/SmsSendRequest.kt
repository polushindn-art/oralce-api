package com.example.oracleapi.dto.sms

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class SmsSendRequest(
    @field:NotBlank(message = "Номер телефона обязателен")
    @field:Pattern(
        regexp = "^\\+?\\d{10,15}$",
        message = "Номер телефона должен быть в формате +7XXXXXXXXXX или 79XXXXXXXXX"
    )
    val phone: String,

    @field:NotBlank(message = "Текст сообщения обязателен")
    val text: String,

    val from: String? = null,  // имя отправителя (альфа-имя)

    val test: Boolean? = null,  // true - тестовый режим (не отправляет реально)

    val ip: String? = null,  // IP адрес пользователя (для защиты от флуда)

    val time: Long? = null,  // время отправки (UNIX TIMESTAMP)

    val ttl: Int? = null,  // срок жизни сообщения в минутах (1-1440)

    val daytime: Boolean? = null,  // учитывать часовой пояс получателя

    val translit: Boolean? = null  // переводит русские символы в латинские
)