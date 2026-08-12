package com.example.oracleapi.dto.sms

data class SmsSendResponse(
    val success: Boolean,
    val message: String,
    val smsId: String? = null,
    val balance: Double? = null,
    val statusCode: Int? = null,
    val statusText: String? = null,
    val details: Map<String, Map<String, Any>>? = null  // для мульти-отправки
)

data class SmsStatusResponse(
    val smsId: String,
    val status: String,  // отправлено, доставлено, ошибка
    val statusCode: Int,
    val statusText: String
)

data class SmsBalanceResponse(
    val balance: Double,
    val limit: Int,
    val usedToday: Int
)