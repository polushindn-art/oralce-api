package com.example.oracleapi.controller

import com.example.oracleapi.common.ProcedureResult
import com.example.oracleapi.dto.common.ApiResponse
import com.example.oracleapi.dto.userlist.RegisteredJsonResponse
import com.example.oracleapi.service.tsdlist.TsdListService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
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
    private val tsdListService: TsdListService
) {
    @GetMapping("/registeredjson")
    @Operation(
        summary = "pkg tsdlist.registeredjson",
        description = "Получить список зарегистрированных ТСД"
    )
    fun registeredjson(
        @RequestParam(required = false) sn: String?,
    ): ResponseEntity<ApiResponse<RegisteredJsonResponse>> {
        return when (val result = tsdListService.getRegisteredSessions(sn)) {
            is ProcedureResult.Success -> {
                ResponseEntity.ok(
                    ApiResponse(
                        success = true,
                        message = "Список получен",
                        data = result.data
                    )
                )
            }

            is ProcedureResult.Error -> {
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                        ApiResponse(
                            success = false,
                            message = result.message
                        )
                    )
            }
        }
    }
}