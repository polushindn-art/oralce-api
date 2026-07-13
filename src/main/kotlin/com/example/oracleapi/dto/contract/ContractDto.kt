package com.example.oracleapi.dto.contract

import com.example.oracleapi.annotation.BindingDateFormat
import com.example.oracleapi.dto.agn.AgnListSimpleDto
import com.example.oracleapi.entity.table.Contract
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDate

/**
 * DTO for {@link com.example.oracleapi.entity.table.Contract}
 */
data class ContractDto(
    val rn: Long? = null,
    val agnlist: Long? = null,
    val agnListEntity: AgnListSimpleDto? = null,
    val ul: Long? = null,
    val ulEntity: AgnListSimpleDto? = null,
    @field:NotNull @field:Size(max = 64)
    val numCantract: String? = null,
    @field:NotNull
    @field:BindingDateFormat
    val begindate: LocalDate? = null,
    @field:BindingDateFormat
    val enddate: LocalDate? = null,
    val limited: Short? = null,
    val status: BigDecimal? = null,
    @field:Size(max = 320) val objectadress: String? = null,
    @field:Size(max = 160) val note: String? = null,
    @field:Size(max = 160) val typesavecontr: String? = null
) : Serializable {
    companion object {
        fun fromEntity(contract: Contract): ContractDto {
            return ContractDto(
                rn = contract.rn,
                agnlist = contract.agnlist,
                agnListEntity = contract.agnlistEntity?.let { AgnListSimpleDto.fromEntity(it) },
                ul = contract.ul,
                ulEntity = contract.agnlistEntity?.let { AgnListSimpleDto.fromEntity(it) },
                numCantract = contract.numCantract,
                begindate = contract.begindate,
                enddate = contract.enddate,
                limited = contract.limited,
                status = contract.status,
                objectadress = contract.objectadress,
                note = contract.note,
                typesavecontr = contract.typesavecontr,
            )
        }
    }
}