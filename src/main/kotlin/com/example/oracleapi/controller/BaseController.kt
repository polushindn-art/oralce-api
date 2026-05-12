package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.common.MetaInfo
import com.example.oracleapi.dto.common.PageResponse
import org.springframework.data.domain.Page

abstract class BaseController {

    protected fun <T> success(page: Page<T>, customMessage: String? = null): MyApiResponse<List<T>> {
        val defaultMessage = "Найдено ${page.totalElements} записей, показано ${page.numberOfElements}"
        return MyApiResponse(
            success = true,
            message = customMessage ?: defaultMessage,
            data = page.content,
            meta = MetaInfo.fromPage(page)
        )
    }

    protected fun <T> success(pageResponse: PageResponse<T>, customMessage: String? = null): MyApiResponse<List<T>> {
        val defaultMessage = "Найдено ${pageResponse.totalElements} записей, показано ${pageResponse.content.size}"
        return MyApiResponse(
            success = true,
            message = customMessage ?: defaultMessage,
            data = pageResponse.content,
            meta = MetaInfo(
                totalElements = pageResponse.totalElements,
                totalPages = pageResponse.totalPages,
                currentPage = pageResponse.pageNumber,
                pageSize = pageResponse.pageSize,
                numberOfElements = pageResponse.content.size,
                first = pageResponse.isFirst,
                last = pageResponse.isLast,
                empty = pageResponse.content.isEmpty()
            )
        )
    }

    protected fun <T> success(data: T, message: String = "Успешно"): MyApiResponse<T> {
        return MyApiResponse.success(data, message)
    }

    protected fun <T> successList(list: List<T>, message: String? = null): MyApiResponse<List<T>> {
        return MyApiResponse.successList(list, message)
    }

    protected fun success(message: String = "Успешно"): MyApiResponse<Unit> {
        return MyApiResponse.success(message)
    }

    protected fun <T> error(message: String, data: T? = null): MyApiResponse<T> {
        return MyApiResponse.error(message, data)
    }
}