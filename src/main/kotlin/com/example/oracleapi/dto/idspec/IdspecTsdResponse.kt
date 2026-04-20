package com.example.oracleapi.dto.idspec

import com.example.oracleapi.entity.Idspec
import java.math.BigDecimal

data class IdspecTsdResponse(
    val rn: Long,
    val nomenId: Long,
    val nomenCode: String?,
    val nomenName: String?,
    val quant: BigDecimal?,
    val summ: BigDecimal?,
    val inprice: BigDecimal?,
    val storein: Long?,
    val storeout: Long?
)

fun Idspec.toTsdResponse(): IdspecTsdResponse = IdspecTsdResponse(
    rn = this.rn!!,
    nomenId = this.nomen?.rn ?: 0,
    nomenCode = this.nomen?.nomencode,
    nomenName = this.nomen?.nomenname,
    quant = this.quant,
    summ = this.summ,
    inprice = this.inprice,
    storein = this.storein,
    storeout = this.storeout
)