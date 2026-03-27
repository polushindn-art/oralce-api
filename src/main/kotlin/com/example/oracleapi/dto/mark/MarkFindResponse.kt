package com.example.oracleapi.dto.mark

data class MarkFindResponse (
    val rn: Long?,
    val dateAdd: String?,
    val km: String,
    val json: String?,
    val cis: String?,
    val gtin: String?,
    val status: Int?,
    val nomen: Long?,
    val stateMark: String?,
    val note: String?
)