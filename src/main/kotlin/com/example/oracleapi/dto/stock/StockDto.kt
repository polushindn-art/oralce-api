package com.example.oracleapi.dto.stock

import com.example.oracleapi.entity.table.Stock
import jakarta.validation.constraints.NotNull
import java.io.Serializable
import java.math.BigDecimal

/**
 * DTO for {@link com.example.oracleapi.entity.table.Stock}
 */
data class StockDto(
    val rn: Long? = null,
    @field:NotNull val storeCode: String? = null,
    @field:NotNull val quanttosale: BigDecimal? = null,
    @field:NotNull val nomenName: String? = null,
    @field:NotNull val measureCode: String? = null,
) : Serializable {
    companion object {
        fun fromEntity(entity: Stock): StockDto {
            return StockDto(
                entity.rn,
                entity.storeEntity?.storecode,
                entity.quanttosale,
                entity.nomenEntity?.nomenname,
                entity.nomenEntity?.measureEntity?.meascode
            )
        }
    }
}