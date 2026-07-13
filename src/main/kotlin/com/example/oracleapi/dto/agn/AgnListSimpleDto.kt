package com.example.oracleapi.dto.agn

import com.example.oracleapi.entity.table.AgnList
import java.io.Serializable
import java.time.LocalDateTime

/**
 * DTO for {@link com.example.oracleapi.entity.table.AgnList}
 */
data class AgnListSimpleDto(
    val rn: Long = 0,
    val crn: Long = 0,
    val agncode: String = "",
    val agnname: String = "",
) : Serializable {
    companion object {
        fun fromEntity(agnList: AgnList): AgnListSimpleDto {
            return AgnListSimpleDto(
                rn = agnList.rn,
                crn = agnList.crn,
                agncode = agnList.agncode,
                agnname = agnList.agnname
            )
        }
    }
}