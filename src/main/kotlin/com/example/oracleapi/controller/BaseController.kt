package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MetaInfo
import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.common.PageResponse
import org.springframework.data.domain.Page

abstract class BaseController {

    // ✅ Добавили удобный метод для работы с PageResponse через MyApiResponse
    protected fun <T> successPage(page: Page<T>, customMessage: String? = null): MyApiResponse<PageResponse<T>> {
        return MyApiResponse.successPage(page, customMessage)
    }

    protected fun <T> successPageResponse(pageResponse: PageResponse<T>, customMessage: String? = null): MyApiResponse<PageResponse<T>> {
        return MyApiResponse.successPageResponse(pageResponse, customMessage)
    }

    // Для пагинации (Spring Page)
    protected fun <T> success(page: Page<T>, customMessage: String? = null): MyApiResponse<List<T>> {
        val defaultMessage = "Найдено ${page.totalElements} записей, показано ${page.numberOfElements}"
        return MyApiResponse(
            success = true,
            message = customMessage ?: defaultMessage,
            data = page.content,
            meta = MetaInfo.fromPage(page)
        )
    }

    // Для списков (без пагинации)
    protected fun <T> successList(list: List<T>, customMessage: String? = null): MyApiResponse<List<T>> {
        val defaultMessage = "Найдено ${list.size} записей"
        return MyApiResponse(
            success = true,
            message = customMessage ?: defaultMessage,
            data = list,
            total = list.size  // ← если есть поле total
        )
    }

    // Для одного объекта
    protected fun <T> success(data: T, message: String = "Успешно"): MyApiResponse<T> {
        return MyApiResponse.success(data, message)
    }

    // Для операций без данных (DELETE)
    protected fun success(message: String = "Успешно"): MyApiResponse<Unit> {
        return MyApiResponse.success(message)
    }

    // Для ошибок
    protected fun <T> error(message: String, data: T? = null): MyApiResponse<T> {
        return MyApiResponse.unsuccess(message, data)
    }
}