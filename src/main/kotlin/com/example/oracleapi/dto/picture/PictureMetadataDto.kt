package com.example.oracleapi.dto.picture

import com.example.oracleapi.entity.table.Picture
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class PictureMetadataDto(
    val rn: Long,
    val datatype: String,
    val extension: String,
    val tablename: String,
    val tablern: Long,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val eventdate: LocalDateTime,
    val pictureSize: Int,
    val hasPreview: Boolean,
    val previewSize: Int? = null
) {
    companion object {
        fun fromEntity(picture: Picture): PictureMetadataDto {
            return PictureMetadataDto(
                rn = picture.rn,
                datatype = picture.datatype,
                extension = picture.datatype,
                tablename = picture.tablename,
                tablern = picture.tablern,
                eventdate = picture.eventdate,
                pictureSize = picture.picture.size,
                hasPreview = picture.preview != null,
                previewSize = picture.preview?.size
            )
        }
    }
}