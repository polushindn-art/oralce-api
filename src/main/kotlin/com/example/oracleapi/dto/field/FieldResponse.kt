package com.example.oracleapi.dto.field

import com.example.oracleapi.entity.table.Field

class FieldResponse(
    val rn: Long,
    val fieldName: String,
    val fieldValue: Long,
    val fieldComment: String,
    val note: String?
) {
    companion object {
        fun fromEntity(field: Field): FieldResponse {
            return FieldResponse(
                rn = field.rn,
                fieldName = field.fieldName,
                fieldValue = field.fieldValue,
                fieldComment = field.fieldComment,
                note = field.note
            )
        }
    }
}

//Ддя статусов
data class StatusResponse(
    val value: Long,
    val comment: String
) {
    companion object {
        fun fromField(field: Field): StatusResponse {
            return StatusResponse(
                value = field.fieldValue,
                comment = field.fieldComment
            )
        }
    }
}