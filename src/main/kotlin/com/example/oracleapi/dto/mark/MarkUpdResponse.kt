package com.example.oracleapi.dto.mark

/**
 * Ответ для процедуры PKG_MARK.UPD
 */
class MarkUpdResponse(
    val success: Boolean,
    val message: String,
    val km: String,
    val executionTimeMs: Long,
    val timestamp: String
)