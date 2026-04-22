package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
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
    ): MyApiResponse<MarkUpdResponse> {
        log.info("PKG_MARK.UPD: km={}", request.km)
        val result = markProcedureService.upd(request)
        return MyApiResponse.success(
            result,
            "Код маркировки успешно обновлен",
            httpRequest.requestURI,
        )


    }
    }
