package com.example.oracleapi.common

/**
 * Единый формат результата
 */
sealed class GeneralResponse<out T> {

    data class Success<T>(
        val data: T,
        val executionTimeMs: Long,
        val timestamp: String
    ) : GeneralResponse<T>()


    data class Error(
        val message: String,
        val errorCode: String? = null,
        val executionTimeMs: Long,
        val timestamp: String
    ) : GeneralResponse<Nothing>()
}