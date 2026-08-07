package com.example.oracleapi.dto.orderpay

import com.example.oracleapi.entity.table.Nomngroup
import com.example.oracleapi.entity.table.Orderpayspec
import jakarta.validation.constraints.NotNull
import java.io.Serializable
import java.math.BigDecimal

/**
 * DTO for {@link com.example.oracleapi.entity.table.Orderpayspec}
 */
data class OrderpayspecDto(
    val rn: Long? = null,
    @field:NotNull
    val nomngroup: Long? = null,
    val nomngroupCode: String? = null,
    @field:NotNull
    val summ: BigDecimal? = null,
    val modified: Long? = null
) : Serializable {
    companion object {
        fun fromEntity(orderpayspec: Orderpayspec): OrderpayspecDto {
            return OrderpayspecDto(
                rn = orderpayspec.rn,
                nomngroup = orderpayspec.nomngroup?.rn,
                nomngroupCode = orderpayspec.nomngroup?.groupcode,
                summ = orderpayspec.summ,
                modified = orderpayspec.modified
            )
        }
    }
}