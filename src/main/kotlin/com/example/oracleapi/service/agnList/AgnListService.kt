package com.example.oracleapi.service.agnList

import com.example.oracleapi.dto.ResponseRN
import com.example.oracleapi.dto.agn.AgnListForUpdResponse
import com.example.oracleapi.dto.agn.AgnListInsResponse
import com.example.oracleapi.dto.agn.AgnListResponse
import com.example.oracleapi.dto.agn.AgnListUpdRequest
import com.example.oracleapi.dto.agn.AgnListUpdResponse
import com.example.oracleapi.dto.agnlist.AgnListInsRequest
import com.example.oracleapi.repository.agnlist.AgnListRepository
import org.springframework.stereotype.Service

@Service
class AgnListService(
    private val agnListIns: AgnListIns,
    private val agnlistRepository: AgnListRepository,
    private val agnListUpd: AgnListUpd,
    private val agnListDel: AgnListDel
) {

    fun existsById(rn: Long): Boolean {
        return agnlistRepository.existsById(rn)
    }

    fun getByRn(rn: Long): AgnListResponse {
        val agnList = agnlistRepository.findByRn(rn) ?: throw IllegalArgumentException("AgnList с RN=$rn не найден")
        return AgnListResponse.fromEntity(agnList)
    }

    fun getByRnForUpdate(rn: Long): AgnListForUpdResponse {
        val agnList = agnlistRepository.findByRn(rn) ?: throw IllegalArgumentException("AgnList с RN=$rn не найден")
        return AgnListForUpdResponse.fromEntity(agnList)
    }

    fun isOurOrg(rn: Long): Boolean {
        // 2 - тип - наше юридическое лицо
        val agn = agnlistRepository.findByRn(rn) ?: throw IllegalArgumentException("Такого ЮЛ не существует")
        return agn.agntype == 2L
    }

    fun ins(request: AgnListInsRequest): AgnListInsResponse {
        return agnListIns.take(request)
    }

    fun und(request: AgnListUpdRequest): AgnListUpdResponse {
        return agnListUpd.take(request)
    }

    fun del(rn: Long): ResponseRN {
        return agnListDel.take(rn)
    }

}