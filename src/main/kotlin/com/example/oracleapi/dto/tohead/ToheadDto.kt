package com.example.oracleapi.dto.tohead

import com.example.oracleapi.annotation.BindingDateFormat
import com.example.oracleapi.dto.agn.AgnListSimpleDto
import com.example.oracleapi.dto.user.UserlistDto
import com.example.oracleapi.entity.table.AgnList
import com.example.oracleapi.entity.table.Tohead
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDate

/**
 * DTO for {@link com.example.oracleapi.entity.table.Tohead}
 */
data class ToheadDto(
    val rn: Long? = null,
    @field:NotNull val doctype: Long? = null,
    @field:NotNull @field:Size(max = 10) val docpref: String? = null,
    @field:BindingDateFormat
    @field:NotNull val docdate: LocalDate? = null,
    @field:NotNull val client: Long? = null,
    val clientEntity: AgnListSimpleDto? = null,
    val paydoc: Long? = null,
    @field:NotNull val docnumb: BigDecimal? = null,
    @field:NotNull val sumdoc: BigDecimal? = null,
    val agnlist: Long? = null,
    val manager: Long? = null,
    val managerEntity: UserlistDto? = null,
) : Serializable {
    companion object {
        fun fromEntity(entity: Tohead): ToheadDto {
            return ToheadDto(
                rn = entity.rn,
                doctype = entity.doctypeEntity?.rn,
                docpref = entity.docpref,
                docdate = entity.docdate,
                client = entity.clientEntity?.rn,
                clientEntity = entity.clientEntity?.let { AgnListSimpleDto.fromEntity(it) },
                paydoc = entity.paydocEntity?.rn,
                docnumb = entity.docnumb,
                sumdoc = entity.sumdoc,
                agnlist = entity.agnlist,
                manager = entity.managerEntity?.rn,
                managerEntity = entity.managerEntity?.let { UserlistDto.fromEntity(it) },
            )
        }
    }
}