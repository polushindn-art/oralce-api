package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.picture.PictureMetadata
import com.example.oracleapi.service.picture.PictureResult
import com.example.oracleapi.service.picture.PictureService
import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/pictures")
@Tag(name = "pkg_picture", description = "Работа с изображениями")
class PictureController(
    private val pictureService: PictureService,
    private val objectMapper: ObjectMapper
) : BaseController() {

    @GetMapping("/{rn}/info")
    @Operation(summary = "Получить информацию об изображении")
    fun getInfo(
        @PathVariable rn: Long
    ): MyApiResponse<PictureMetadata> {
        val metadata = pictureService.getMetadata(rn)
            ?: throw IllegalArgumentException("Изображение с RN=$rn не найдено")
        return success(metadata)
    }

    @GetMapping("/{rn}/view")
    @Operation(summary = "Просмотр изображения")
    fun view(
        @PathVariable rn: Long,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        handlePictureResponse(pictureService.getPictureFile(rn, isPreview = false), "inline", null, request, response)
    }

    @GetMapping("/{rn}/download")
    @Operation(summary = "Скачать изображение")
    fun download(
        @PathVariable rn: Long,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        handlePictureResponse(pictureService.getPictureFile(rn, isPreview = false), "attachment", "image_$rn", request, response)
    }

    @GetMapping("/{rn}/preview")
    @Operation(summary = "Просмотр миниатюры")
    fun preview(
        @PathVariable rn: Long,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        handlePictureResponse(pictureService.getPictureFile(rn, isPreview = true), "inline", null, request, response)
    }

    @GetMapping("/{rn}/preview/download")
    @Operation(summary = "Скачать миниатюру")
    fun downloadPreview(
        @PathVariable rn: Long,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        handlePictureResponse(pictureService.getPictureFile(rn, isPreview = true), "attachment", "preview_$rn", request, response)
    }

    @GetMapping("/by-tablern/{tablern}")
    @Operation(summary = "Получить список RN изображений по tablern")
    fun getRnListByTablern(
        @PathVariable tablern: Long
    ): MyApiResponse<List<Long>> {
        return successList(pictureService.getRnListByTablernNotDeleted(tablern))
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================

    private fun handlePictureResponse(
        result: PictureResult,
        disposition: String,
        filenamePrefix: String?,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        when (result) {
            is PictureResult.NotFound -> {
                sendErrorResponse(response, result.message, request.requestURI)
            }
            is PictureResult.Success -> {
                val contentType = getContentType(result.datatype)
                response.contentType = contentType

                if (disposition == "attachment" && filenamePrefix != null) {
                    val extension = result.datatype.lowercase()
                    response.setHeader("Content-Disposition", "attachment; filename=\"$filenamePrefix.$extension\"")
                } else {
                    response.setHeader("Content-Disposition", "inline")
                }

                response.outputStream.write(result.data)
                response.outputStream.flush()
            }
        }
    }

    private fun sendErrorResponse(
        response: HttpServletResponse,
        message: String,
        path: String
    ) {
        response.status = HttpStatus.NOT_FOUND.value()
        response.contentType = "application/json;charset=UTF-8"

        val errorResponse = MyApiResponse.unsuccess<Nothing>(
            message = message,
            path = path
        )

        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }

    private fun getContentType(extension: String): String {
        return when (extension.lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            "bmp" -> "image/bmp"
            else -> "application/octet-stream"
        }
    }
}