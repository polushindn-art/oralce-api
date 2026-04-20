package com.example.oracleapi.controller

import com.example.oracleapi.dto.BarcodeGenerateRequest
import com.example.oracleapi.dto.common.ApiResponse
import com.example.oracleapi.service.BarcodeService
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
@RequestMapping("/barcode")
@Tag(name = "05. Штрихкоды", description = "Генерация GS1 DataMatrix штрихкодов")
class BarcodeController(
    private val barcodeService: BarcodeService,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(BarcodeController::class.java)

    @PostMapping("/gs1/datamatrix/download")
    @Operation(summary = "Сгенерировать и скачать GS1 DataMatrix")
    fun download(
        @Valid @RequestBody request: BarcodeGenerateRequest,
        response: HttpServletResponse
    ) {
        log.info(
            "Скачивание GS1 DataMatrix: data={}, size={}x{}",
            request.data.take(50), request.width, request.height
        )

        try {
            val imageBytes = barcodeService.generateGs1DataMatrix(
                 request.data,  // ← правильное имя параметра
                 request.width,
                 request.height
            )

            val filename = "gs1_barcode_${System.currentTimeMillis()}.png"

            response.contentType = "image/png"
            response.setHeader("Content-Disposition", "attachment; filename=\"$filename\"")
            response.outputStream.write(imageBytes)
            response.outputStream.flush()

        } catch (e: Exception) {
            log.error("Ошибка генерации штрихкода: ${e.message}", e)
            sendErrorResponse(
                response = response,
                message = "Ошибка генерации штрихкода: ${e.message}",
                path = "/barcode/gs1/datamatrix/download"
            )
        }
    }

    @GetMapping("/gs1/datamatrix/download")
    @Operation(summary = "Сгенерировать и скачать GS1 DataMatrix (GET)")
    fun downloadGet(
        @RequestParam data: String,
        @RequestParam(defaultValue = "300") width: Int,
        @RequestParam(defaultValue = "300") height: Int,
        response: HttpServletResponse
    ) {
        log.info(
            "Скачивание GS1 DataMatrix (GET): data={}, size={}x{}",
            data.take(50), width, height
        )

        try {
            val imageBytes = barcodeService.generateGs1DataMatrix(
                data,  // ← правильное имя параметра
                width,
                height
            )

            val filename = "gs1_barcode_${System.currentTimeMillis()}.png"

            response.contentType = "image/png"
            response.setHeader("Content-Disposition", "attachment; filename=\"$filename\"")
            response.outputStream.write(imageBytes)
            response.outputStream.flush()

        } catch (e: Exception) {
            log.error("Ошибка генерации штрихкода: ${e.message}", e)
            sendErrorResponse(
                response = response,
                message = "Ошибка генерации штрихкода: ${e.message}",
                path = "/barcode/gs1/datamatrix/download"
            )
        }
    }

    @PostMapping("/gs1/datamatrix/view")
    @Operation(summary = "Просмотр GS1 DataMatrix")
    fun view(
        @Valid @RequestBody request: BarcodeGenerateRequest,
        response: HttpServletResponse
    ) {
        log.info(
            "Просмотр GS1 DataMatrix: data={}, size={}x{}",
            request.data.take(50), request.width, request.height
        )

        try {
            val imageBytes = barcodeService.generateGs1DataMatrix(
                request.data,  // ← правильное имя параметра
                request.width,
                request.height
            )

            response.contentType = "image/png"
            response.setHeader("Content-Disposition", "inline")
            response.outputStream.write(imageBytes)
            response.outputStream.flush()

        } catch (e: Exception) {
            log.error("Ошибка генерации штрихкода: ${e.message}", e)
            sendErrorResponse(
                response = response,
                message = "Ошибка генерации штрихкода: ${e.message}",
                path = "/barcode/gs1/datamatrix/view"
            )
        }
    }

    @GetMapping("/gs1/datamatrix/view")
    @Operation(summary = "Просмотр GS1 DataMatrix (GET)")
    fun viewGet(
        @RequestParam data: String,
        @RequestParam(defaultValue = "300") width: Int,
        @RequestParam(defaultValue = "300") height: Int,
        response: HttpServletResponse
    ) {
        log.info(
            "Просмотр GS1 DataMatrix (GET): data={}, size={}x{}",
            data.take(50), width, height
        )

        try {
            val imageBytes = barcodeService.generateGs1DataMatrix(
                data,  // ← правильное имя параметра
                width,
                height
            )

            response.contentType = "image/png"
            response.setHeader("Content-Disposition", "inline")
            response.outputStream.write(imageBytes)
            response.outputStream.flush()

        } catch (e: Exception) {
            log.error("Ошибка генерации штрихкода: ${e.message}", e)
            sendErrorResponse(
                response = response,
                message = "Ошибка генерации штрихкода: ${e.message}",
                path = "/barcode/gs1/datamatrix/view"
            )
        }
    }

    @PostMapping("/gs1/datamatrix/info")
    @Operation(summary = "Информация о GS1 DataMatrix")
    fun getInfo(
        @Valid @RequestBody request: BarcodeGenerateRequest,
        httpRequest: HttpServletRequest
    ): ApiResponse<Map<String, Any>> {
        log.info("Информация о GS1 DataMatrix: data={}", request.data.take(50))

        val imageBytes = barcodeService.generateGs1DataMatrix(
            request.data,  // ← правильное имя параметра
            request.width,
            request.height
        )

        val info = mapOf(
            "dataLength" to request.data.length,
            "width" to request.width,
            "height" to request.height,
            "imageSizeBytes" to imageBytes.size,
            "matrixSize" to calculateMatrixSize(request.data)
        )

        return ApiResponse.success(
            data = info,
            message = "Информация получена",
            path = httpRequest.requestURI
        )
    }

    private fun sendErrorResponse(
        response: HttpServletResponse,
        message: String,
        path: String
    ) {
        response.status = HttpStatus.BAD_REQUEST.value()
        response.contentType = "application/json;charset=UTF-8"

        val errorResponse = ApiResponse.error<Nothing>(
            message = message,
            path = path
        )

        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }

    private fun calculateMatrixSize(data: String): String {
        val length = data.length
        return when {
            length <= 12 -> "10x10"
            length <= 20 -> "12x12"
            length <= 36 -> "14x14"
            length <= 44 -> "16x16"
            length <= 72 -> "18x18"
            length <= 98 -> "20x20"
            length <= 132 -> "22x22"
            length <= 172 -> "24x24"
            length <= 214 -> "26x26"
            else -> "32x32"
        }
    }
}