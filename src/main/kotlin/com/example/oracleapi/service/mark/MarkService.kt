package com.example.oracleapi.service.mark

import com.example.oracleapi.dto.mark.MarkUpdRequest
import com.example.oracleapi.dto.mark.MarkUpdResponse
import com.example.oracleapi.dto.mark.ParseMarkResponse
import com.example.oracleapi.dto.vMark.MarkSearchResponse
import com.example.oracleapi.repository.mark.MarkRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MarkService(
    private val parseMark: ParseMark,
    private val markUPD: MarkUPD,
    private val markSearch: MarkSearch,
    private val markRepository: MarkRepository,
) {

    @Transactional(readOnly = true)
    fun parseMarkCode(km: String): ParseMarkResponse {
        return parseMark.take(km)
    }

    @Transactional
    fun updateMark(request: MarkUpdRequest): MarkUpdResponse {
        return markUPD.take(request)
    }

    @Transactional(readOnly = true)
    fun searchMark(km: String): MarkSearchResponse {
        return markSearch.findMark(km)
    }

    @Transactional(readOnly = true)
    fun getKmBySpec(spec:Long):List<String> {
        return markRepository.getKmBySpecRn(spec)
    }

}