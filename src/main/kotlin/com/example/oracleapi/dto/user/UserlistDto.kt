package com.example.oracleapi.dto.user

import com.example.oracleapi.dto.agn.AgnListSimpleDto
import com.example.oracleapi.entity.table.Userlist
import java.io.Serializable

/**
 * DTO for {@link com.example.oracleapi.entity.table.Userlist}
 */
data class UserlistDto(
    val rn: Long? = null,
    val usercode: String? = null,
    val useragn: Long? = null,
    val agnListEntry: AgnListSimpleDto? = null
) : Serializable {
    companion object {
        fun fromEntity(entity: Userlist): UserlistDto {
            return UserlistDto(
                entity.rn,
                entity.usercode,
                entity.useragn,
                entity.agnListEntry?.let { AgnListSimpleDto.fromEntity(it) }
            )
        }
    }
}
