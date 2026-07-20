package com.example.oracleapi.service.mark

import com.example.oracleapi.dto.mark.MarkUpdRequest
import com.example.oracleapi.dto.mark.MarkUpdResponse
import com.example.oracleapi.dto.mark.ParseMarkResponse
import com.example.oracleapi.dto.protocolMark.ProtocolMarkRequest
import com.example.oracleapi.dto.vMark.MarkSearchResponse
import com.example.oracleapi.entity.table.Mark
import com.example.oracleapi.entity.table.ProtocolMark
import com.example.oracleapi.repository.mark.MarkRepository
import com.example.oracleapi.service.protocolMark.ProtocolMarkService
import org.springframework.stereotype.Service

@Service
class MarkService(
    private val parseMark: ParseMark,
    private val markUPD: MarkUPD,
    private val markSearch: MarkSearch,
    private val markRepository: MarkRepository,
    private val protocolMarkService: ProtocolMarkService
) {
    fun parseMarkCode(km: String): ParseMarkResponse {
        return parseMark.take(km)
    }

    fun updateMark(request: MarkUpdRequest): MarkUpdResponse {
        return markUPD.take(request)
    }

    fun searchMark(km: String): MarkSearchResponse {
        return markSearch.findMark(km)
    }

    fun getKmBySpec(spec:Long):List<String> {
        return markRepository.getKmBySpecRn(spec)
    }

}