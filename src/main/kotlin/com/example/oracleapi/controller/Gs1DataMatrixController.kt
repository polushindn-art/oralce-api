package com.example.oracleapi.controller

import com.example.oracleapi.dto.BarcodeGenerateRequest
import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.mark.MarkFindRequest
import com.example.oracleapi.dto.mark.MarkFindResponse
import com.example.oracleapi.service.GS1DataMatrixService
import com.example.oracleapi.service.MarkingCodeParserService
import com.example.oracleapi.service.mark.MarkProcedureService
import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/barcode")
@Tag(name = "Коды маркировки", description = "Генерация DataMatrix штрихкодов")
class Gs1DataMatrixController(
    private val barcodeService: GS1DataMatrixService,
    private val markProcedureService: MarkProcedureService,
    private val objectMapper: ObjectMapper
) : BaseController() {
    private val log = LoggerFactory.getLogger(Gs1DataMatrixController::class.java)

    // ==================== ГЕНЕРАЦИЯ (автоопределение формата) ====================

    @PostMapping("/generate")
    @Operation(summary = "Сгенерировать DataMatrix (автоопределение формата)")
    fun generate(
        @Valid @RequestBody request: BarcodeGenerateRequest,
        response: HttpServletResponse
    ) {
        log.info("Генерация: data={}, size={}x{}", request.data.take(50), request.width, request.height)

        try {
            val imageBytes = barcodeService.generateAutoDataMatrix(
                request.data,
                request.width,
                request.height
            )

            response.contentType = "image/png"
            response.setHeader(
                "Content-Disposition",
                "attachment; filename=\"barcode_${System.currentTimeMillis()}.png\""
            )
            response.outputStream.write(imageBytes)
            response.outputStream.flush()

        } catch (e: Exception) {
            log.error("Ошибка: ${e.message}", e)
            sendErrorResponse(response, e.message ?: "Неизвестная ошибка", "/barcode/generate")
        }
    }

    // ==================== ПОИСК КМ ====================

    @GetMapping("/find")
    @Operation(summary = "Поиск по КМ", description = "Поиск кода маркировки в представлении v_mark_find")
    fun find(
        @RequestParam km: String
    ): MyApiResponse<MarkFindResponse> {
        log.info("Поиск по КМ: km={}", km)
        val request = MarkFindRequest(km = km)
        val result = markProcedureService.find(request)
        return success(result, "Код маркировки найден")
    }

    private fun sendErrorResponse(response: HttpServletResponse, message: String, path: String) {
        response.status = HttpStatus.BAD_REQUEST.value()
        response.contentType = "application/json;charset=UTF-8"
        val errorResponse = MyApiResponse.error<Nothing>(message = message, path = path)
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }

}