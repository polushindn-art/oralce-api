package com.example.oracleapi.controller

import com.example.oracleapi.common.ProcedureResult
import com.example.oracleapi.dto.common.ApiResponse
import com.example.oracleapi.dto.public.GenIdResponse
import com.example.oracleapi.dto.public.GetNomenByBarcodeRequest
import com.example.oracleapi.dto.public.GetNomenByBarcodeResponse
import com.example.oracleapi.service.public.PublicProcedureService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/public")
@Tag(name = "pkg_public", description = "Процедуры пакета PKG_PUBLIC")
class PublicController(
    private val publicProcedureService: PublicProcedureService
) {
    @PostMapping("/getNomenByBarcode")
    @Operation(
        summary = "pkg_public.getnomenbybarcode",
        description = "Получить идентификатор номенклатуры по штрих-коду"
    )
    fun getNomenByBarcode(@Valid @RequestBody(required = true) request: GetNomenByBarcodeRequest): ResponseEntity<ApiResponse<GetNomenByBarcodeResponse>> {
        return when (val result = publicProcedureService.getNomenByBarcode(request)) {
            is ProcedureResult.Success -> {
                ResponseEntity.ok(
                    ApiResponse(
                        success = true,
                        message = "Идентификатор получен",
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

    @GetMapping("/genIdRn")
    @Operation(
        summary = "pkg_public.genidrn",
        description = "Получить уникальный идентификатор RN"
    )
    fun genIdRn(): ResponseEntity<ApiResponse<GenIdResponse>> {
        return when (val result = publicProcedureService.getIdRn()) {
            is ProcedureResult.Success -> {
                ResponseEntity.ok(
                    ApiResponse(
                        success = true,
                        message = "Идентификатор RN получен",
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

    @GetMapping("/genIdRn/multiple")
    @Operation(summary = "Получить несколько RN", description = "Генерирует несколько уникальных идентификаторов")
    fun generateMultipleRn(
        @RequestParam(defaultValue = "3") count: Int,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<GenIdResponse>> {

        return when (val result = publicProcedureService.generateMultipleRn(count)) {
            is ProcedureResult.Success -> {
                ResponseEntity.ok(
                    ApiResponse.success(
                        data = result.data,
                        message = "Сгенерировано ${result.data.count} идентификаторов",
                        path = request.servletPath
                    )
                )
            }
            is ProcedureResult.Error -> {
                when {
                    result.message.contains("Count must be") -> {
                        ResponseEntity.badRequest().body(
                            ApiResponse.error(
                                message = result.message,
                                path = request.servletPath
                            )
                        )
                    }
                    else -> {
                        ResponseEntity.internalServerError().body(
                            ApiResponse.error(
                                message = result.message,
                                path = request.servletPath
                            )
                        )
                    }
                }
            }
        }
    }

}