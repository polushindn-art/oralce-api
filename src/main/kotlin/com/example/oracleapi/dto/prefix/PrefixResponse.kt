package com.example.oracleapi.dto.prefix

data class PrefixResponse(
    val id: Long,
    val docpref: String,
    val docprefnew: String?,
    val note: String?,
    val divisionCode: String?
)
