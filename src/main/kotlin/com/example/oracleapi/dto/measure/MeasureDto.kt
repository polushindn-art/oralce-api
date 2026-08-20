package com.example.oracleapi.dto.measure

import com.example.oracleapi.entity.table.Measure
import jakarta.persistence.Entity
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.io.Serializable

/**
 * DTO for {@link com.example.oracleapi.entity.table.Measure}
 */
data class MeasureDto(
    val rn: Long? = null,
    @field:NotNull @field:Size(max = 20) val meascode: String? = null,
    @field:NotNull @field:Size(max = 80) val measname: String? = null,
    @field:NotNull val typemeas: Long? = null,
    @field:Size(max = 80) val note: String? = null,
    @field:NotNull val tag2108: Short? = null
) : Serializable {
    companion object {
        fun fromEntity(entity: Measure): MeasureDto {
            return MeasureDto(
                rn = entity.rn,
                meascode = entity.meascode,
                measname = entity.measname,
                typemeas = entity.typemeas,
                note = entity.note,
                tag2108 = entity.tag2108
            )
        }
    }
}