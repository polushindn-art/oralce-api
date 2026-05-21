package com.example.oracleapi.dto.idhead

import com.example.oracleapi.entity.Idhead
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import java.math.BigDecimal
import java.time.LocalDateTime

@JsonPropertyOrder("rn", "docpref") //порядок полей в ответе
data class IdheadResponse(
    val rn: Long,
    val crn: Long,
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
    val usercode: String?
)

fun Idhead.toResponse(): IdheadResponse = IdheadResponse(
    rn = this.rn,
    crn = this.crn,
    doctypeCode = this.doctypeEntity!!.doccode,
    docpref = this.docpref!!,
    docdate = this.docdate,
    docnumb = this.docnumb,
    sumprice = this.sumprice,
    idStatus = this.idStatus!!,
    statusCode = this.statusEntity?.fieldComment,
    storeinCode = this.storeInEntity?.storecode,
    storeoutCode = this.storeOutEntity?.storecode,
    provider = this.provider,
    note = this.note,
    manager = this.manager,
    storeoper = this.storeoper,
    usercode = this.userListEntity?.agnListEntry?.agncode,
)