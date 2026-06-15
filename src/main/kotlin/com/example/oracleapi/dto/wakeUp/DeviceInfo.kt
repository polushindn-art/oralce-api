package com.example.oracleapi.dto.wakeUp

import java.time.LocalDateTime

data class DeviceInfo(
    var pingCount: Int = 0,
    var lastPingAt: LocalDateTime? = null
)
