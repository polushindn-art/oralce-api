package com.example.oracleapi.dto.agn

import com.example.oracleapi.entity.table.AgnList

data class AgnListResponse(
    /**Идентификатор*/
    val rn: Long,
    /**Мнемокод контрагента*/
    val agncode: String,
    /**Наименование контрагента*/
    val agnname: String,
    /**ИНН*/
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