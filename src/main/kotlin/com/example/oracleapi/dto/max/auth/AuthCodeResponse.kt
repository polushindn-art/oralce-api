package com.example.oracleapi.dto.max.auth

data class AuthCodeResponse(
    val success: Boolean,
    val code: String? = null,
    val phone: String? = null,
    val message: String? = null
)
