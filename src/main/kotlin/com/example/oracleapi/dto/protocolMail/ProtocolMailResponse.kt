package com.example.oracleapi.dto.protocolMail

import com.example.oracleapi.entity.ProtocolMail

data class ProtocolMailResponse(
    val result: Long? = 0L,
    val errName: String? = null,
) {
    companion object {
        fun fromEntity(protocolMail: ProtocolMail): ProtocolMailResponse {
            return ProtocolMailResponse(
                result = protocolMail.result,
                errName = protocolMail.errname
            )
        }
    }
}

