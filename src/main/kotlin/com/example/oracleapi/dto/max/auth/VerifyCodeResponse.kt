package com.example.oracleapi.dto.max.auth

data class VerifyCodeResponse(
    val success: Boolean,
    val chatId: String? = null,
    val message: String? = null
)