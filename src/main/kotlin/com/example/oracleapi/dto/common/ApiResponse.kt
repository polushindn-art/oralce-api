package com.example.oracleapi.dto.common

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Универсальный ответ API для всех эндпоинтов
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T> (
    val success: Boolean,
    val message: String,
    val data: T? = null,

    @get:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    val timestamp: LocalDateTime = LocalDateTime.now(),

    val path: String? = null
) {
    companion object {
        fun <T> success(
            data: T,
            message: String = "Операция выполнена успешно",
            path: String? = null
        ): ApiResponse<T> {
            return ApiResponse(
                success = true,
                message = message,
                data = data,
                path = path
            )
        }

        fun <T> success(
            message: String = "Операция выполнена успешно",
            path: String? = null
        ): ApiResponse<Unit> {
            return ApiResponse(
                success = true,
                message = message,
                data = null,
                path = path
            )
        }

        fun <T> error(
            message: String,
            data: T? = null,
            path: String? = null
        ): ApiResponse<T> {
            return ApiResponse(
                success = false,
                message = message,
                data = data,
                path = path
            )
        }

        fun <T> error(
            exception: Exception,
            path: String? = null
        ): ApiResponse<T> {
            return ApiResponse(
                success = false,
                message = exception.message ?: "Внутренняя ошибка сервера",
                data = null,
                path = path
            )
        }
    }
}