package com.example.oracleapi.common

/**
 * Единый формат результата для всех процедур
 */
sealed class ProcedureResult<out T> {

    data class Success<T>(
        val data: T,
        val executionTimeMs: Long,
        val timestamp: String
    ) : ProcedureResult<T>()


    data class Error(
        val message: String,
        val errorCode: String? = null,
        val executionTimeMs: Long,
        val timestamp: String
    ) : ProcedureResult<Nothing>()
}