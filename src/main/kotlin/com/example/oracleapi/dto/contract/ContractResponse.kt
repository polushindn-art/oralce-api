package com.example.oracleapi.dto.contract

import com.example.oracleapi.entity.table.Contract
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDate

/**
 * DTO for {@link com.example.oracleapi.entity.table.Contract}
 */
data class ContractResponse(
    val rn: Long? = null,
    val agnlist: Long? = null,
    val ul: Long? = null,
    @field:NotNull @field:Size(max = 64) val numCantract: String? = null,
    @field:NotNull val begindate: LocalDate? = null,
    val enddate: LocalDate? = null,
    val limited: Short? = null,
    val status: BigDecimal? = null,
    @field:Size(max = 320) val objectadress: String? = null,
    @field:Size(max = 160) val note: String? = null,
    val contrdata: ByteArray? = null,
    @field:Size(max = 160) val typesavecontr: String? = null
) : Serializable {
    companion object {
        fun fromEntity(contract: Contract): ContractResponse {
            return ContractResponse(
                rn = contract.rn,
                agnlist = contract.agnlist,
                ul = contract.ul,
                numCantract = contract.numCantract,
                begindate = contract.begindate,
                enddate = contract.enddate,
                limited = contract.limited,
                status = contract.status,
                objectadress = contract.objectadress,
                note = contract.note,
                contrdata = contract.contrdata,
                typesavecontr = contract.typesavecontr,
            )
        }
    }
}