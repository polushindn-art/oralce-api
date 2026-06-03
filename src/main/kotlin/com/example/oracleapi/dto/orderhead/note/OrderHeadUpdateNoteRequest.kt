package com.example.oracleapi.dto.orderhead.note

data class OrderHeadUpdateNoteRequest(
    val orderhead: Long? = null,
    val note: String? = null,
)
