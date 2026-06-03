package com.example.oracleapi.service.agnList

import com.example.oracleapi.dto.agn.AgnListResponse
import com.example.oracleapi.repository.agnlist.AgnListRepository
import org.springframework.stereotype.Service

@Service
class AgnListService(
    private val agnlistRepository: AgnListRepository
) {

    fun existsById(rn: Long): Boolean {
        return agnlistRepository.existsById(rn)
    }

    fun getByRn(rn: Long): AgnListResponse {
        val agnList = agnlistRepository.findByRn(rn)
            ?: throw IllegalArgumentException("AgnList с RN=$rn не найден")
        return AgnListResponse.fromEntity(agnList)
    }

    fun isOurOrg(rn: Long): Boolean {
        // 2 - тип - наше юридическое лицо
        val agn = agnlistRepository.findByRn(rn) ?: throw IllegalArgumentException("Такого ЮЛ не существует")
        return agn.agntype == 2L
    }

}