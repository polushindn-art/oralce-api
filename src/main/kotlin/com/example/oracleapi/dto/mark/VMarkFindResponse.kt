package com.example.oracleapi.dto.mark
import com.example.oracleapi.entity.view.VMarkFind
import java.time.LocalDate
import java.time.LocalDateTime

data class VMarkFindResponse(
    val rn: Long?,
    val dateAdd: LocalDateTime?,
    val km: String?,
    val cis: String?,
    val gtin: String?,
    val status: Long?,
    val nomen: Long?,
    val stateMark: LocalDate?,
    val note: String?,
    val err: String?,
    val cisInfo: Map<String, Any?>?
) {
    companion object {
        fun fromEntity(entity: VMarkFind): VMarkFindResponse {
            return VMarkFindResponse(
                rn = entity.rn,
                dateAdd = entity.dateAdd,
                km = entity.km,
                cis = entity.cis,
                gtin = entity.gtin,
                status = entity.status,
                nomen = entity.nomen,
                stateMark = entity.stateMark,
                note = entity.note,
                err = entity.err,
                cisInfo = entity.getCisInfo()
            )
        }
    }
}