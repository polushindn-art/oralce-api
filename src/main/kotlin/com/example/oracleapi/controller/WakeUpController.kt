package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.wakeUp.InfoResponse
import com.example.oracleapi.dto.wakeUp.PingRequest
import com.example.oracleapi.service.wakeUp.WakeUpService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("v1/wakeup")
class WakeUpController(
    private val wakeUpService: WakeUpService
) : BaseController() {
    @PostMapping("/ping")
    fun ping(@RequestBody request: PingRequest): MyApiResponse<Map<String, String>> {
        wakeUpService.ping(request.device)
        return success((mapOf("status" to "ok")))
    }

    @GetMapping("/info")
    fun info(@RequestParam device: String): MyApiResponse<InfoResponse> {
        val infoDev = wakeUpService.getInfo(device)

        return if (infoDev != null) {
            success(
                InfoResponse(
                    deviceId = device,
                    infoDev.pingCount,
                    infoDev.lastPingAt
                )
            )
        } else {
            error(
                InfoResponse(
                    deviceId = device,
                    pingCount = 0,
                    lastPingAt = null
                )
            )
        }
    }

    @DeleteMapping("/reset")
    fun reset(@RequestParam device: String): MyApiResponse<Map<String, Any>> {
        val oldCount = wakeUpService.reset(device)
        return success(
            mapOf(
                "device" to device,
                "reset" to true,
                "oldPingCount" to oldCount
            )
        )
    }
}