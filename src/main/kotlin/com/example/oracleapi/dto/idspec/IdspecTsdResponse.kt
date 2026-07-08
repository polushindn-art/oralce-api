package com.example.oracleapi.dto.idspec

import com.example.oracleapi.entity.Idspec
import java.math.BigDecimal

data class IdspecTsdResponse(
    val rn: Long,
    val nomenId: Long,
    val nomenCode: String?,
    val article: String?,
    val nomenName: String?,
    val quant: BigDecimal?,
    val summ: BigDecimal?,
    val inprice: BigDecimal?,
    val storein: Long?,
    val storeout: Long?,
    val km: List<String>? = emptyList()
) {
    // Конструктор для JPQL
    constructor(
        rn: Long,
        nomenId: Long,
        nomenCode: String?,
        article: String?,
        nomenName: String?,
        quant: BigDecimal?,
        summ: BigDecimal?,
        inprice: BigDecimal?,
        storein: Long?,
        storeout: Long?
    ) : this(
        rn = rn,
        nomenId = nomenId,
        nomenCode = nomenCode,
        article = article,
        nomenName = nomenName,
        quant = quant,
        summ = summ,
        inprice = inprice,
        storein = storein,
        storeout = storeout,
        km = emptyList()
    )
}

fun Idspec.toTsdResponse(): IdspecTsdResponse = IdspecTsdResponse(
    rn = this.rn!!,
    nomenId = this.nomen?.rn ?: 0,
    nomenCode = this.nomen?.nomencode,
    article = this.nomen?.article,
    nomenName = this.nomen?.nomenname,
    quant = this.quant,
    summ = this.summ,
    inprice = this.inprice,
    storein = this.storein,
    storeout = this.storeout
)