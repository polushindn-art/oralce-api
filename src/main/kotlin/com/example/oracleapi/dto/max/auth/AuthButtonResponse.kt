package com.example.oracleapi.dto.max.auth

data class AuthButtonResponse(
    val success: Boolean,
    val phone: String? = null,
    val message: String? = null
)
