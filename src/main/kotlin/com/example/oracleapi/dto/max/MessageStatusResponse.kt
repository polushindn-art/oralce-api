package com.example.oracleapi.dto.max

data class MessageStatusResponse(
    val success: Boolean,
    val messageId: String,
    val chatId: String,
    val status: String,  // "sent", "delivered", "read", "failed"
    val deliveredAt: Long? = null,
    val readAt: Long? = null,
    val error: String? = null
)
