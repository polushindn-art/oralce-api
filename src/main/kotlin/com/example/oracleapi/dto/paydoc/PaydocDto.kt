package com.example.oracleapi.dto.paydoc

import com.example.oracleapi.annotation.BindingDateTimeFormat
import com.example.oracleapi.entity.table.Paydoc
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * DTO for {@link com.example.oracleapi.entity.table.Paydoc}
 */
data class PaydocDto(
    val rn: Long? = null,
    @field:NotNull
    @field:Size(max = 10)
    @field:Schema(name = "Префикс документа")
    val docpref: String? = null,
    @field:BindingDateTimeFormat
    @field:NotNull
    val docdate: LocalDateTime? = null,
    @field:Size(max = 80)
    val payinfo: String? = null,
    @field:NotNull
    val paysumm: BigDecimal? = null,
    @field:NotNull
    val ndsrate: BigDecimal? = null,
    @field:Size(max = 320)
    val note: String? = null,
    @field:NotNull
    val docnumb: BigDecimal? = null,
    @field:Size(max = 10)
    val accountcor: String? = null,
    @field:Size(max = 80)
    val application: String? = null,
    @field:NotNull
    val ppayStatus: Long? = null,
    @field:NotNull
    val loadsumm: BigDecimal? = null,
    @field:Size(max = 40)
    val payment: String? = null,
    @field:NotNull
    val modified: Long? = null,
    val ndssum: BigDecimal? = null,
    val basesum: BigDecimal? = null
) : Serializable {
    companion object {
        fun fromEntity(paydoc: Paydoc): PaydocDto {
            return PaydocDto(
                rn = paydoc.rn,
                docpref = paydoc.docpref,
                docdate = paydoc.docdate,
                payinfo = paydoc.payinfo,
                paysumm = paydoc.paysumm,
                ndsrate = paydoc.ndsrate,
                note = paydoc.note,
                docnumb = paydoc.docnumb,
                accountcor = paydoc.accountcor,
                application = paydoc.application,
                ppayStatus = paydoc.ppayStatus,
                loadsumm = paydoc.loadsumm,
                payment = paydoc.payment,
                modified = paydoc.modified,
                ndssum = paydoc.ndssum,
                basesum = paydoc.basesum,
            )
        }


    }
}