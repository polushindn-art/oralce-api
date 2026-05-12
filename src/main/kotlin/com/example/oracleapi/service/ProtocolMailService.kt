package com.example.oracleapi.service

import com.example.oracleapi.dto.protocolMail.ProtocolMailResponse
import com.example.oracleapi.entity.ProtocolMail
import com.example.oracleapi.repository.ProtocolMailRepository
import org.springframework.stereotype.Service


@Service
class ProtocolMailService(
    private val protocolMailRepository: ProtocolMailRepository
) {
    fun getInfoByRn(rn: Long): ProtocolMailResponse {
        val entity = protocolMailRepository.findByRn(rn)
        return entity.toResponse()
    }

    fun ProtocolMail.toResponse(): ProtocolMailResponse {
        return ProtocolMailResponse.fromEntity(this)
    }
}
