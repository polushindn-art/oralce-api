package com.example.oracleapi.dto.sendmail

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Ответ на отправку письма")
data class SimpleEmailResponse(
    @field:Schema(description = "Успешность операции", example = "true")
    val success: Boolean,

    @field:Schema(description = "Сообщение о результате", example = "Письмо успешно отправлено")
    val message: String,

    @field:Schema(description = "Идентификатор письма в таблице PROTOCOL_MAIL", example = "12345")
    val mailRn: Long? = null
)