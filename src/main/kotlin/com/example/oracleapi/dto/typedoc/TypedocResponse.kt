package com.example.oracleapi.dto.typedoc

import com.example.oracleapi.entity.table.Typedoc

data class TypedocResponse(
    val rn: Long,
    val doccode: String,
    val docname: String,
    val divisionCode: String? = null,
    val divisionName: String? = null
) {
    companion object {
        fun fromEntity(typedoc: Typedoc): TypedocResponse {
            return TypedocResponse(
                rn = typedoc.rn,
                doccode = typedoc.doccode,
                docname = typedoc.docname,
                divisionCode = typedoc.divisionEntity?.divisioncode,
                divisionName = typedoc.divisionEntity?.divisionname
            )
        }
    }
}
