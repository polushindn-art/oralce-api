package com.example.oracleapi.controller

import com.example.oracleapi.common.ProcedureResult
import com.example.oracleapi.dto.common.ApiResponse
import com.example.oracleapi.dto.mark.MarkUpdRequest
import com.example.oracleapi.dto.mark.MarkUpdResponse
import com.example.oracleapi.service.mark.MarkProcedureService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
        @Valid @RequestBody request: MarkUpdRequest
    ): ResponseEntity<ApiResponse<MarkUpdResponse>> {
        log.info("PKG_MARK.UPD: km={}", request.km)
        return when (val result = markProcedureService.upd(request)) {
            is ProcedureResult.Success -> {
                ResponseEntity.ok(
                    ApiResponse(
                        success = true,
                        message = "Код маркировки успешно обновлен",
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
