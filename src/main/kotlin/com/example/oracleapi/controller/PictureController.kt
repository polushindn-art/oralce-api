package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.picture.PictureMetadata
import com.example.oracleapi.entity.Picture
import com.example.oracleapi.service.picture.PictureService
import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
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
        val picture = getPictureOrThrow(rn)
        return success(PictureMetadata.fromEntity(picture))
    }

    @GetMapping("/{rn}/view")
    @Operation(summary = "Просмотр изображения")
    fun view(
        @PathVariable rn: Long,
        response: HttpServletResponse
    ) {
        val picture = getPictureOrNull(rn)

        if (picture == null) {
            sendErrorResponse(response, "Изображение с RN=$rn не найдено", "/pictures/$rn/view")
            return
        }

        val contentType = getContentType(picture.datatype)
        response.contentType = contentType
        response.setHeader("Content-Disposition", "inline")
        response.outputStream.write(picture.picture)
        response.outputStream.flush()
    }

    @GetMapping("/{rn}/download")
    @Operation(summary = "Скачать изображение")
    fun download(
        @PathVariable rn: Long,
        response: HttpServletResponse
    ) {
        val picture = getPictureOrNull(rn)

        if (picture == null) {
            sendErrorResponse(response, "Изображение с RN=$rn не найдено", "/pictures/$rn/download")
            return
        }

        val extension = picture.datatype.lowercase()
        val filename = "image_$rn.$extension"
        val contentType = getContentType(picture.datatype)

        response.contentType = contentType
        response.setHeader("Content-Disposition", "attachment; filename=\"$filename\"")
        response.outputStream.write(picture.picture)
        response.outputStream.flush()
    }

    @GetMapping("/{rn}/preview")
    @Operation(summary = "Просмотр миниатюры")
    fun preview(
        @PathVariable rn: Long,
        response: HttpServletResponse
    ) {
        val picture = getPictureOrNull(rn)

        if (picture == null) {
            sendErrorResponse(response, "Изображение с RN=$rn не найдено", "/pictures/$rn/preview")
            return
        }

        if (picture.preview == null) {
            sendErrorResponse(response, "Миниатюра для изображения с RN=$rn не найдена", "/pictures/$rn/preview")
            return
        }

        val contentType = getContentType(picture.datatype)
        response.contentType = contentType
        response.setHeader("Content-Disposition", "inline")
        response.outputStream.write(picture.preview)
        response.outputStream.flush()
    }

    @GetMapping("/{rn}/preview/download")
    @Operation(summary = "Скачать миниатюру")
    fun downloadPreview(
        @PathVariable rn: Long,
        response: HttpServletResponse
    ) {
        val picture = getPictureOrNull(rn)

        if (picture == null) {
            sendErrorResponse(response, "Изображение с RN=$rn не найдено", "/pictures/$rn/preview/download")
            return
        }

        if (picture.preview == null) {
            sendErrorResponse(
                response,
                "Миниатюра для изображения с RN=$rn не найдена",
                "/pictures/$rn/preview/download"
            )
            return
        }

        val extension = picture.datatype.lowercase()
        val filename = "preview_$rn.$extension"
        val contentType = getContentType(picture.datatype)

        response.contentType = contentType
        response.setHeader("Content-Disposition", "attachment; filename=\"$filename\"")
        response.outputStream.write(picture.preview)
        response.outputStream.flush()
    }

    @GetMapping("/by-tablern/{tablern}")
    @Operation(summary = "Получить список RN изображений по tablern")
    fun getRnListByTablern(
        @PathVariable tablern: Long
    ): MyApiResponse<List<Long>> {
        return successList(pictureService.getRnListByTablernNotDeleted(tablern))
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

    private fun getPictureOrNull(rn: Long): Picture? {
        return pictureService.getPictureByRn(rn)
    }

    private fun getPictureOrThrow(rn: Long): Picture {
        return pictureService.getPictureByRn(rn)
            ?: throw IllegalArgumentException("Изображение с RN=$rn не найдено")
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