package com.example.oracleapi.dto.sms

import jakarta.validation.constraints.NotEmpty

data class SmsMultiSendRequest(
    @field:NotEmpty(message = "Список сообщений не может быть пустым")
    val messages: Map<String, String>,  // phone -> text

    val from: String? = null,
    val test: Boolean? = null,
    val ip: String? = null,
    val time: Long? = null,
    val ttl: Int? = null,
    val daytime: Boolean? = null,
    val translit: Boolean? = null
)