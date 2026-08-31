package com.example.oracleapi.dto.idhead

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import java.math.BigDecimal
import java.time.LocalDateTime

@JsonPropertyOrder("rn", "docpref") //порядок полей в ответе
data class IdheadResponse(
    val rn: Long,
    val crn: Long,
    val typedoc: Long,
    val doctypeCode: String,
    val docpref: String,
    val docdate: LocalDateTime?,
    val docnumb: BigDecimal?,
    val sumprice: BigDecimal?,
    val idStatus: Long,
    val statusCode: String?,
    val storeinCode: String?,
    val storeoutCode: String?,
    val provider: Long?,
    val note: String?,
    val manager: Long?,
    val storeoper: Long?,
    val storeoperCode: String?,
    val storeoperName: String?,
    val usercode: String?,
    val sumweight: BigDecimal?,
    val sumvolume: BigDecimal?
)