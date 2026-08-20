package com.example.oracleapi.dto.tohead

import com.example.oracleapi.annotation.BindingDateFormat
import com.example.oracleapi.dto.agn.AgnListSimpleDto
import com.example.oracleapi.dto.tospec.TospecDto
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.LocalDate

data class ToHeadWithSpec(
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
    val spec: List<TospecDto>,
)
