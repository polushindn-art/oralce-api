package com.example.oracleapi.controller

import com.example.oracleapi.service.ErrorCounter
import com.example.oracleapi.service.ErrorStats
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/monitoring")
class MonitoringController(private val errorCounter: ErrorCounter) {

    @GetMapping("/errors-hour")
    fun getErrorsStats(): ErrorStats {
        return errorCounter.getStats()
    }
}