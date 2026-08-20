package com.example.oracleapi.dto.tospec

import com.example.oracleapi.dto.nomnlist.NomnlistDto
import com.example.oracleapi.dto.store.StoreDto
import com.example.oracleapi.entity.table.Measunit
import com.example.oracleapi.entity.table.Store
import com.example.oracleapi.entity.table.Tospec
import jakarta.validation.constraints.NotNull
import java.io.Serializable
import java.math.BigDecimal

/**
 * DTO for {@link com.example.oracleapi.entity.table.Tospec}
 */
data class TospecDto(
    val rn: Long? = null,
    @field:NotNull val nomenEntity: NomnlistDto? = null,
    @field:NotNull val price: BigDecimal? = null,
    @field:NotNull val priceout: BigDecimal? = null,
    @field:NotNull val quant: BigDecimal? = null,
    @field:NotNull val summ: BigDecimal? = null,
    @field:NotNull val storeEntity: StoreDto? = null,
    val weight: BigDecimal? = null,
    val volume: BigDecimal? = null
) : Serializable {
    companion object {
        fun fromEntity(entity: Tospec): TospecDto {
            return TospecDto(
                entity.rn,
                entity.nomenEntity?.let { NomnlistDto.fromEntity(it) },
                price = entity.price,
                priceout = entity.priceout,
                quant = entity.quant,
                summ = entity.summ,
                storeEntity = entity.storeEntity?.let { StoreDto.fromEntity(it) },
                weight = entity.weight,
                volume = entity.volume,
            )
        }
    }
}