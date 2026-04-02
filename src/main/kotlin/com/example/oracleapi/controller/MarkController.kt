package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.ApiResponse
import com.example.oracleapi.dto.mark.MarkFindRequest
import com.example.oracleapi.dto.mark.MarkFindResponse
import com.example.oracleapi.dto.mark.MarkUpdRequest
import com.example.oracleapi.dto.mark.MarkUpdResponse
import com.example.oracleapi.service.mark.MarkProcedureService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/mark")
@Tag(name = "pkg_mark", description = "Пакет Oracle для работы с кодами маркировки")
class MarkController(
    private val markProcedureService: MarkProcedureService
) {
    private val log = LoggerFactory.getLogger(MarkController::class.java)

    @PostMapping("/upd")
    @Operation(summary = "PKG_MARK.UPD", description = "Обновление записи")
    fun upd(
        @Valid @RequestBody request: MarkUpdRequest,
        httpRequest: HttpServletRequest
    ): ApiResponse<MarkUpdResponse> {
        log.info("PKG_MARK.UPD: km={}", request.km)
        val result = markProcedureService.upd(request)
        return ApiResponse.success(
            result,
            "Код маркировки успешно обновлен",
            httpRequest.requestURI,
        )


    }

    @GetMapping("/find")
    @Operation(summary = "Поиск по КМ", description = "Поиск кода маркировки в представлении v_mark_find")
    fun find(
        @RequestParam km: String,
        httpRequest: HttpServletRequest
    ): ApiResponse<MarkFindResponse> {
        log.info("Поиск по КМ: km={}", km)
        val request = MarkFindRequest(km = km)
        val result = markProcedureService.find(request)
        return ApiResponse.success(
            data = result,
            message = "Код маркировки найден",
            httpRequest.requestURI
        )

    }
}
