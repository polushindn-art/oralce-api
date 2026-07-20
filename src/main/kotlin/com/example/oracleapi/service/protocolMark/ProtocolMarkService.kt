package com.example.oracleapi.service.protocolMark

import com.example.oracleapi.dto.protocolMark.ProtocolMarkRequest
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class ProtocolMarkService(
    private val protocolMarkIns: ProtocolMarkIns
) {
    @Async
    fun logMark(request: ProtocolMarkRequest) {
        protocolMarkIns.take(request)
    }
}