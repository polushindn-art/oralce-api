package com.example.oracleapi.dto.wakeUp

import java.time.LocalDateTime

data class InfoResponse(
    val deviceId: String,
    val pingCount: Int,
    val lastPingAt: LocalDateTime?
)