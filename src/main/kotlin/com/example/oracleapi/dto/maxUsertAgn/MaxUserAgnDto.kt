package com.example.oracleapi.dto.maxUsertAgn

import com.example.oracleapi.annotation.BindingDateTimeFormat
import com.example.oracleapi.entity.table.MaxUserAgn
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime

/**
 * DTO for {@link com.example.oracleapi.entity.table.MaxUserAgn}
 */
data class MaxUserAgnDto(
    val rn: Long? = null,
    @field:NotNull @field:Size(max = 100) val userId: String? = null,
    @field:NotNull @field:Size(max = 100) val chatId: String? = null,
    @field:Size(max = 500) val userName: String? = null,
    @field:NotNull @field:Size(max = 50) val phone: String? = null,
    @field:NotNull @field:Size(max = 50) val phoneTail: String? = null,
    @field:Size(max = 50) val botType: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val isActive: Boolean? = null
) : Serializable {
    companion object {

        fun fromEntity(entity: MaxUserAgn): MaxUserAgnDto {
            return MaxUserAgnDto(
                rn = entity.rn,
                userId = entity.userId,
                chatId = entity.chatId,
                userName = entity.userName,
                phone = entity.phone,
                phoneTail = entity.phoneTail,
                botType = entity.botType,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                isActive = entity.isActive
            )
        }

        fun fromEntityList(entities: List<MaxUserAgn>): List<MaxUserAgnDto> {
            return entities.map { fromEntity(it) }
        }

    }
}