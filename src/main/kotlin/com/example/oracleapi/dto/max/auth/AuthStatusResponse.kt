package com.example.oracleapi.dto.max.auth

data class AuthStatusResponse(
    val authorized: Boolean,
    val chatId: String? = null,
    val phone: String? = null
)
