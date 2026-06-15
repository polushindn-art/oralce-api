package com.example.oracleapi.dto.agn

import com.example.oracleapi.entity.table.AgnList

data class AgnListResponse(
    val rn: Long,
    val agncode: String,
    val agnname: String,
    val inn: String?

) {
    companion object {
        fun fromEntity(agnList: AgnList): AgnListResponse {
            return AgnListResponse(
                rn = agnList.rn,
                agncode = agnList.agncode,
                agnname = agnList.agnname,
                inn = agnList.agnidnumb
            )
        }
    }
}