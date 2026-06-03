package com.example.oracleapi.service.mark

import com.example.oracleapi.dto.mark.MarkUpdRequest
import com.example.oracleapi.dto.mark.MarkUpdResponse
import com.example.oracleapi.dto.mark.ParseMarkResponse
import org.springframework.stereotype.Service

@Service
class MarkService(
    private val parseMark: ParseMark,
    private val markUPD: MarkUPD
) {
    fun parseMarkCode(km: String): ParseMarkResponse {
        return parseMark.take(km)
    }

    fun updateMark(request: MarkUpdRequest): MarkUpdResponse {
        return markUPD.take(request)
    }

}