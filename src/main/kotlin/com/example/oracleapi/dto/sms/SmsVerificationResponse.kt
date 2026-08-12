package com.example.oracleapi.dto.sms

data class SmsVerificationResponse(
    val phone: String,
    val code: String,
    val smsId: String? = null,
    val balance: Double? = null
)