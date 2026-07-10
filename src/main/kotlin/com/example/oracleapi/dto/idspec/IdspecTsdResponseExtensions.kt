package com.example.oracleapi.dto.idspec

import jakarta.persistence.Tuple
import java.math.BigDecimal

fun Tuple.toIdspecTsdResponse(): IdspecTsdResponse {
    val kmsString = this.get("kms", String::class.java)
    val kmList = if (!kmsString.isNullOrBlank()) {
        kmsString.split(" ").filter { it.isNotBlank() }
    } else {
        emptyList()
    }

    return IdspecTsdResponse(
        rn = (this.get("rn") as Number).toLong(),
        nomenId = this.get("nomenId", BigDecimal::class.java).toLong(),
        nomenCode = this.get("nomenCode", String::class.java),
        article = this.get("article", String::class.java),
        nomenName = this.get("nomenName", String::class.java),
        quant = this.get("quant", BigDecimal::class.java),
        summ = this.get("summ", BigDecimal::class.java),
        inprice = this.get("inprice", BigDecimal::class.java),
        storein = (this.get("storein") as? Number)?.toLong(),
        storeout = (this.get("storeout") as? Number)?.toLong(),
        km = kmList
    )
}