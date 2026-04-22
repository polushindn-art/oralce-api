package com.example.oracleapi.controller

import com.example.oracleapi.dto.JsonResponseView
import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.tsdlist.Registeredjson
import com.example.oracleapi.dto.tsdlist.UsedJson
import com.example.oracleapi.service.tsdlist.TsdListService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tsdlist")
@Tag(name = "tsdlist", description = "Процедуры пакета PKG TSDLIST")
class TsdListController(
    private val tsdListService: TsdListService,
) {
    @GetMapping("/registeredjson")
    @Operation(
        summary = "pkg tsdlist.registeredjson",
        description = "Получить список зарегистрированных ТСД"
    )
    fun registeredjson(
        @RequestParam(required = false) sn: String?,
        request: HttpServletRequest
    ): MyApiResponse<JsonResponseView<Registeredjson>> {
        val result = tsdListService.getRegisteredSessions(sn)
        return MyApiResponse.success(
            data = result,
            "Список получен",
            path = request.requestURI
        )
    }

    @GetMapping("/usedtsd")
    @Operation(
        summary = "usedtsd",
        description = "Получить список активных пользователй ТСД"
    )
    fun usedTsd(
        @RequestParam(required = false) pbe: Long?,
        request: HttpServletRequest
    ): MyApiResponse<JsonResponseView<UsedJson>> {
        val data = tsdListService.getUsedTsd(pbe)
        return MyApiResponse.success(
            data = data,
            message = "Список активных пользователей ТСД успешно получен",
            path = request.requestURI
        )
    }
}