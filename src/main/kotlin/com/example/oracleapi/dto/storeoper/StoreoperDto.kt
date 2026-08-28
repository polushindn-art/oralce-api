package com.example.oracleapi.dto.storeoper

import com.example.oracleapi.entity.table.Storeoper
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.io.Serializable

/**
 * DTO for {@link com.example.oracleapi.entity.table.Storeoper}
 */
data class StoreoperDto(
    /**Уникальный идентификатор*/
    val rn: Long? = null,
    /**Операция*/
    @field:NotNull @field:Size(max = 20) val stropercode: String? = null,
    /**Операция*/
    @field:NotNull @field:Size(max = 80) val stropername: String? = null,
    /**Примечание*/
    @field:Size(max = 80) val note: String? = null
) : Serializable {
    companion object {
        fun fromEntity(entity: Storeoper): StoreoperDto {
            return StoreoperDto(
                rn = entity.rn,
                stropercode = entity.stropercode,
                stropername = entity.stropername,
                note = entity.note
            )
        }
    }
}