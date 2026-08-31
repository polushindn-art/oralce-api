package com.example.oracleapi.mapper

import com.example.oracleapi.dto.idhead.IdheadResponse
import com.example.oracleapi.entity.table.Idhead

fun Idhead.toResponse(): IdheadResponse = IdheadResponse(
    rn = this.rn,
    crn = this.crn,
    typedoc = this.doctype,
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
    storeoperCode = this.storeoperEntity?.stropercode,
    storeoperName = this.storeoperEntity?.stropername,
    usercode = this.userListEntity?.agnListEntry?.agncode,
    sumweight = this.sumweight,
    sumvolume = this.sumvolume
)