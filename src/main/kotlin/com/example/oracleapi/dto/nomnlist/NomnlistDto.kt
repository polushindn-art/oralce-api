package com.example.oracleapi.dto.nomnlist

import com.example.oracleapi.dto.measure.MeasureDto
import com.example.oracleapi.entity.table.Nomnlist
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.io.Serializable

/**
 * DTO for {@link com.example.oracleapi.entity.table.Nomnlist}
 */
data class NomnlistDto(
    val rn: Long? = null,
    @field:NotNull @field:Size(max = 20) val nomencode: String? = null,
    @field:NotNull @field:Size(max = 160) val nomenname: String? = null,
    @field:Size(max = 17) val article: String? = null,
    @field:NotNull val measureEntity: MeasureDto? = null,
) : Serializable {
    companion object {
        fun fromEntity(entity: Nomnlist): NomnlistDto {
            return NomnlistDto(
                rn = entity.rn,
                nomencode = entity.nomencode,
                nomenname = entity.nomenname,
                article = entity.article,
                measureEntity = entity.measureEntity?.let { MeasureDto.fromEntity(it) }
            )
        }
    }
}