package com.example.oracleapi.dto.contract

import com.example.oracleapi.entity.table.Contractval
import com.example.oracleapi.entity.table.Orderpayspec
import java.io.Serializable
import java.math.BigDecimal

/**
 * DTO for {@link com.example.oracleapi.entity.table.Contractval}
 */
data class ContractvalDto(
    val rn: Long? = null,
    val deliveryDays: BigDecimal? = null,
    val orderpay: BigDecimal? = null,
    val calendarDay: BigDecimal? = null,
    val nomngroup: Long? = null,
    val checkdi: Boolean? = null
) : Serializable {
    companion object {
        fun fromEntity(entity: Contractval): ContractvalDto {
            return ContractvalDto(
                rn = entity.rn,
                deliveryDays = entity.deliveryDays,
                orderpay = entity.orderpay,
                calendarDay = entity.calendarDay,
                nomngroup = entity.nomngroup,
                checkdi = entity.checkdi
            )
        }
    }
}