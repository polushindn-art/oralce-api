package com.example.oracleapi.dto.idhead

import com.example.oracleapi.entity.Idhead
import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.LocalDateTime

data class IdheadResponse(
    val rn: Long,
    val crn: Long,
    val doctypeCode: String,
    val docpref: String,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val docdate: LocalDateTime?,
    val docnumb: BigDecimal?,
    val sumprice: BigDecimal?,
    val idStatus: Long,
    val storeinCode: String?,
    val storeoutCode: String?,
    val provider: Long?,
    val note: String?,
    val manager: Long?,
    val storeoper: Long?,
)

fun Idhead.toResponse(): IdheadResponse = IdheadResponse(
    rn = this.rn!!,
    crn = this.crn!!,
    doctypeCode = this.doctypeEntity!!.doccode,
    docpref = this.docpref!!,
    docdate = this.docdate,
    docnumb = this.docnumb,
    sumprice = this.sumprice,
    idStatus = this.idStatus!!,
    storeinCode = this.storeInEntity?.storecode,
    storeoutCode = this.storeOutEntity?.storecode,
    provider = this.provider,
    note = this.note,
    manager = this.manager,
    storeoper = this.storeoper
)