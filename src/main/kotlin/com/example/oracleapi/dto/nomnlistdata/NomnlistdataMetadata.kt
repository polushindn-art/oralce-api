package com.example.oracleapi.dto.nomnlistdata

import com.example.oracleapi.entity.nomnlistdata.Nomnlistdata

data class NomnlistdataMetadata(
    val rn: Long,
    val nomen: Long,           // RN номенклатуры
    val needDownload: Boolean, // нужно ли скачивать
    val created: String?,
    val height: Int?,          // высота
    val width: Int?,           // ширина
    val size: Long?            // размер в байтах
) {
    companion object {
        fun fromEntity(entity: Nomnlistdata): NomnlistdataMetadata {
            return NomnlistdataMetadata(
                rn = entity.rn ?: 0,
                nomen = entity.nomen ?: 0,
                needDownload = (entity.needdownload as? Number)?.toInt() == 1,
                created = entity.created as? String,
                height = (entity.height as? Number)?.toInt(),
                width = (entity.width as? Number)?.toInt(),
                size = (entity.imageData as? Number)?.toLong()
            )
        }
    }
}

data class NomnlistdataRequest(
    val nomen: Long,
    val photonum: Int = 1,
    val needDownload: Boolean = false,
    val manager: String? = null
)
