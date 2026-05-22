package com.example.oracleapi.exception

class OracleException(
    val oracleCode: Int,
    override val message: String,
    val sqlState: String? = null,
    val details: String? = null,
    val nestedErrors: List<OracleError>? = null
) : RuntimeException(message)

data class OracleError(
    val code: Int,
    val message: String
)