package com.example.oracleapi.controller

import com.example.oracleapi.Helper
import com.example.oracleapi.common.GeneralResponse
import com.example.oracleapi.dto.JsonResponseView
import com.example.oracleapi.dto.common.ApiResponse
import com.example.oracleapi.dto.tsdlist.Registeredjson
import com.example.oracleapi.dto.tsdlist.UsedJson
import com.example.oracleapi.service.tsdlist.TsdListService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tsdlist")
@Tag(name = "pkg_tsdlist", description = "Процедуры пакета PKG TSDLIST")
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
    ): ResponseEntity<ApiResponse<JsonResponseView<Registeredjson>>> {
        return when (val result = tsdListService.getRegisteredSessions(sn)) {
            is GeneralResponse.Success -> {
                ResponseEntity.ok(
                    ApiResponse(
                        true,
                        "Список получен",
                        result.data
                    )
                )
            }

            is GeneralResponse.Error -> {
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                        ApiResponse(
                            success = false,
                            message = "Не удалось получить данные по ТСД $sn"
                        )
                    )
            }
        }
    }

    @GetMapping("/usedtsd")
    @Operation(
        summary = "usedtsd",
        description = "Получить список активных пользователй ТСД"
    )
    fun usedTsd(
        @RequestParam(required = false) pbe: Long?,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<JsonResponseView<UsedJson>>> {

        return try {
            val data = tsdListService.getUsedTsd(pbe)
            ResponseEntity.ok(
                ApiResponse.success(
                    data = data,
                    message = "Список активных пользователей ТСД успешно получен",
                    path = request.requestURI
                )
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                    ApiResponse.error(
                        message = e.message ?: "Неверный параметр pbe",
                        path = request.requestURI
                    )
                )
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                    ApiResponse.error(
                        message = "Ошибка при получении данных: ${e.message}",
                        path = request.requestURI
                    )
                )
        }
    }

}