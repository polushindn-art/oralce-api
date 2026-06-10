package com.example.oracleapi.service.markBinding

import com.example.oracleapi.dto.markBinding.MarkBindingRequest
import com.example.oracleapi.dto.markBinding.MarkBindingResponse
import org.springframework.stereotype.Service

@Service
class MarkBindingService(
    private val markBindingIns: MarkBindingIns
) {
    fun ins(request: MarkBindingRequest): MarkBindingResponse {
        return markBindingIns.take(request)
    }
}
