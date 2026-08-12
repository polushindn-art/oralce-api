package com.example.oracleapi.dto.max

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Результат отправки сообщения")
data class MessageResponse(
    @field:Schema(description = "Успешно ли отправлено")
    val success: Boolean,

    @field:Schema(description = "ID сообщения в MAX (если успешно)")
    val messageId: String? = null,

    @field:Schema(description = "Ошибка (если есть)")
    val error: String? = null,

    val message: String? = null
)