package com.example.oracleapi.dto.common

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import org.springframework.data.domain.Page
import java.time.LocalDateTime

/**
 * Универсальный ответ API для всех эндпоинтов
 * data в конце для лучшей читаемости
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("success", "message", "timestamp", "path", "meta", "total", "data")  // ← порядок полей
data class MyApiResponse<T> (
    val success: Boolean,
    val message: String,
    @get:JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss.SSS")
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val path: String? = null,
    val total: Int? = null,
    val meta: MetaInfo? = null,
    val data: T? = null  // ← data в конце
) {
    companion object {

        // ========== SUCCESS METHODS ==========

        fun <T> success(
            data: T,
            message: String = "Операция выполнена успешно",
            path: String? = null
        ): MyApiResponse<T> {
            return MyApiResponse(
                success = true,
                message = message,
                data = data,
                path = path
            )
        }

        fun success(
            message: String = "Операция выполнена успешно",
            path: String? = null
        ): MyApiResponse<Unit> {
            return MyApiResponse(
                success = true,
                message = message,
                data = null,
                path = path
            )
        }

        fun <T> successList(
            data: List<T>,
            message: String? = null,
            path: String? = null
        ): MyApiResponse<List<T>> {
            val defaultMessage = "Найдено записей ${data.size}"
            return MyApiResponse(
                success = true,
                total = data.size,
                message = message ?: defaultMessage,
                data = data,
                path = path
            )
        }

        fun <T> successPage(
            page: Page<T>,
            customMessage: String? = null,
            path: String? = null
        ): MyApiResponse<PageResponse<T>> {
            val pageResponse = PageResponse.fromPage(page)
            val defaultMessage = "Найдено записей ${page.totalElements}, показано ${page.numberOfElements}"
            return MyApiResponse(
                success = true,
                message = customMessage ?: defaultMessage,
                data = pageResponse,
                path = path
            )
        }

        fun <T> successPageResponse(
            pageResponse: PageResponse<T>,
            customMessage: String? = null,
            path: String? = null
        ): MyApiResponse<PageResponse<T>> {
            val defaultMessage = "Найдено записей ${pageResponse.totalElements}, показано ${pageResponse.content.size}"
            return MyApiResponse(
                success = true,
                message = customMessage ?: defaultMessage,
                data = pageResponse,
                path = path
            )
        }

        fun <T> successWithMeta(
            data: T,
            meta: MetaInfo,
            message: String = "Операция выполнена успешно",
            path: String? = null
        ): MyApiResponse<T> {
            return MyApiResponse(
                success = true,
                message = message,
                data = data,
                meta = meta,
                path = path
            )
        }

        // ========== ERROR METHODS ==========

        fun <T> unsuccess(
            message: String,
            data: T? = null,
            path: String? = null
        ): MyApiResponse<T> {
            return MyApiResponse(
                success = false,
                message = message,
                data = data,
                path = path
            )
        }

        fun <T> unsuccess(
            exception: Exception,
            path: String? = null
        ): MyApiResponse<T> {
            return MyApiResponse(
                success = false,
                message = exception.message ?: "Внутренняя ошибка сервера",
                data = null,
                path = path
            )
        }

        fun validationError(
            message: String,
            errors: Map<String, String>? = null,
            path: String? = null
        ): MyApiResponse<Map<String, String>> {
            return MyApiResponse(
                success = false,
                message = message,
                data = errors,
                path = path
            )
        }
    }
}

data class MetaInfo(
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int,
    val numberOfElements: Int,
    val first: Boolean,
    val last: Boolean,
    val empty: Boolean
) {
    companion object {
        fun fromPage(page: Page<*>): MetaInfo {
            return MetaInfo(
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                currentPage = page.number,
                pageSize = page.size,
                numberOfElements = page.numberOfElements,
                first = page.isFirst,
                last = page.isLast,
                empty = page.isEmpty
            )
        }
    }
}