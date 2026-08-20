package com.example.oracleapi.dto.store

import com.example.oracleapi.entity.table.Store
import java.io.Serializable

/**
 * DTO for {@link com.example.oracleapi.entity.table.Store}
 */
data class StoreDto(
    val rn: Long? = null,
    val storepbe: Long? = null,
    val storecode: String? = null,
    val storename: String? = null,
    val note: String? = null
) : Serializable {
    companion object {
        fun fromEntity(store: Store): StoreDto {
            return StoreDto(
                rn = store.rn,
                storepbe = store.storepbe,
                storecode = store.storecode,
                storename = store.storename,
                note = store.note
            )
        }
    }
}