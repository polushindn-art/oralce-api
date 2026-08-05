package com.example.oracleapi.dto.max

data class MessageStatus(
    val id: String,
    val status: String,
    val chatId: String,
    val timestamp: Long,
    val senderId: String,
    val text: String? = null
)
