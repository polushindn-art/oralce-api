package com.example.oracleapi.service.wakeUp

import com.example.oracleapi.dto.wakeUp.DeviceInfo
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

@Service
class WakeUpService {
    private val devices = ConcurrentHashMap<String, DeviceInfo>()

    fun ping(deviceId: String) {
        val info = devices.getOrPut(deviceId) { DeviceInfo() }
        info.pingCount++
        info.lastPingAt = LocalDateTime.now()
    }

    fun getInfo(deviceId: String): DeviceInfo? {
        return devices[deviceId]
    }

    fun reset(deviceId: String): Int {
        val info = devices[deviceId]
        return if (info != null) {
            val oldCount = info.pingCount
            info.pingCount = 0
            info.lastPingAt = null
            oldCount
        } else {
            0
        }
    }

}