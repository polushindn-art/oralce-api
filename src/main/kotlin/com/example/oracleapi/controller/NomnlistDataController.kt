package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.nomnlistdata.NomnlistdataMetadataDto
import com.example.oracleapi.service.nomnlistdata.DownloadResult
import com.example.oracleapi.service.nomnlistdata.NomnlistdataService
import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("v1/nomnlistdata")
@Tag(name = "NomnListData", description = "Работа с фотографиями номенклатуры")
class NomnlistDataController(
    private val service: NomnlistdataService,
    private val objectMapper: ObjectMapper
) : BaseController() {

    // ==================== INFO ====================

    @GetMapping("/by-nomen/{nomen}/info")
    @Operation(summary = "Получить список всех фото для номенклатуры")
    fun getInfoByNomen(
        @PathVariable nomen: Long
    ): MyApiResponse<List<NomnlistdataMetadataDto>> {
        return successList(service.getInfoByNomen(nomen))
    }

    @GetMapping("/by-nomen/{nomen}/info/{photonum}")
    @Operation(summary = "Получить информацию о конкретном фото номенклатуры")
    fun getPhotoInfoByNomen(
        @PathVariable nomen: Long,
        @PathVariable photonum: Int
    ): MyApiResponse<NomnlistdataMetadataDto> {
        val metadata = service.getPhotoInfoByNomen(nomen, photonum)
            ?: return error("Фото для номенклатуры $nomen (№$photonum) не найдено")
        return success(metadata)
    }

    // ==================== VIEW ====================

    @GetMapping("/by-nomen/{nomen}/view/{photonum}")
    @Operation(summary = "Просмотр конкретного фото номенклатуры")
    fun viewByNomenAndPhotonum(
        @PathVariable nomen: Long,
        @PathVariable photonum: Int,
        response: HttpServletResponse
    ) {
        val data = service.getPhotoData(nomen, photonum, isPreview = false)
        if (data == null) {
            sendErrorResponse(response, "Фото для номенклатуры $nomen (№$photonum) не найдено или пустое", "/v1/nomnlistdata/by-nomen/$nomen/view/$photonum")
            return
        }

        writeImageToResponse(response, data, "inline")
    }

    @GetMapping("/by-nomen/{nomen}/main")
    @Operation(summary = "Просмотр главного фото номенклатуры (photonum=1)")
    fun viewMainByNomen(
        @PathVariable nomen: Long,
        response: HttpServletResponse
    ) {
        val data = service.getMainPhotoData(nomen)
        if (data == null) {
            sendErrorResponse(response, "Главное фото для номенклатуры $nomen не найдено", "/v1/nomnlistdata/by-nomen/$nomen/main")
            return
        }

        writeImageToResponse(response, data, "inline")
    }

    @GetMapping("/by-nomen/{nomen}/view")
    @Operation(summary = "Просмотр первого фото номенклатуры (photonum=1)")
    fun viewFirstByNomen(
        @PathVariable nomen: Long,
        response: HttpServletResponse
    ) {
        val data = service.getFirstPhotoData(nomen)
        if (data == null) {
            sendErrorResponse(response, "Фото для номенклатуры $nomen не найдено", "/v1/nomnlistdata/by-nomen/$nomen/view")
            return
        }

        writeImageToResponse(response, data, "inline")
    }

    // ==================== DOWNLOAD ====================

    @GetMapping("/by-nomen/{nomen}/download/{photonum}")
    @Operation(summary = "Скачать конкретное фото номенклатуры")
    fun downloadByNomenAndPhotonum(
        @PathVariable nomen: Long,
        @PathVariable photonum: Int,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        handleDownload(service.getPhotoDownloadData(nomen, photonum, isPreview = false), request, response)
    }

    // ==================== PREVIEW ====================

    @GetMapping("/by-nomen/{nomen}/preview/{photonum}")
    @Operation(summary = "Просмотр превью конкретного фото номенклатуры")
    fun previewByNomenAndPhotonum(
        @PathVariable nomen: Long,
        @PathVariable photonum: Int,
        response: HttpServletResponse
    ) {
        val data = service.getPhotoData(nomen, photonum, isPreview = true)
        if (data == null) {
            sendErrorResponse(response, "Превью для фото не найдено", "/v1/nomnlistdata/by-nomen/$nomen/preview/$photonum")
            return
        }

        writeImageToResponse(response, data, "inline")
    }

    @GetMapping("/by-nomen/{nomen}/preview/download/{photonum}")
    @Operation(summary = "Скачать превью конкретного фото номенклатуры")
    fun downloadPreviewByNomenAndPhotonum(
        @PathVariable nomen: Long,
        @PathVariable photonum: Int,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        handleDownload(service.getPhotoDownloadData(nomen, photonum, isPreview = true), request, response)
    }

    // ==================== CRUD ====================

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "Загрузить новое фото для номенклатуры")
    fun upload(
        @RequestParam nomen: Long,
        @RequestParam file: MultipartFile,
        @RequestParam(required = false) needDownload: Boolean?
    ): MyApiResponse<NomnlistdataMetadataDto> {
        return try {
            val metadata = service.uploadPhoto(
                nomen = nomen,
                file = file,
                needDownload = needDownload
            )
            success(metadata)
        } catch (e: IllegalArgumentException) {
            error(message = e.message.toString())
        }
    }

    @DeleteMapping("/by-nomen/{nomen}/{photonum}")
    @Operation(summary = "Удалить конкретное фото номенклатуры")
    fun deleteByNomenAndPhotonum(
        @PathVariable nomen: Long,
        @PathVariable photonum: Int,
        request: HttpServletRequest
    ): MyApiResponse<Unit> {
        val deleted = service.softDeleteByNomenAndPhotonum(nomen, photonum)

        if (!deleted) {
            return MyApiResponse.unsuccess(
                message = "Фото для номенклатуры $nomen (№$photonum) не найдено",
                path = request.requestURI
            )
        }

        return MyApiResponse.success(
            message = "Фото для номенклатуры $nomen (№$photonum) удалено",
            path = request.requestURI
        )
    }

    @DeleteMapping("/by-nomen/{nomen}")
    @Operation(summary = "Удалить все фото для номенклатуры")
    fun deleteAllByNomen(
        @PathVariable nomen: Long,
        request: HttpServletRequest
    ): MyApiResponse<Unit> {
        service.softDeleteByNomen(nomen)
        return MyApiResponse.success(
            message = "Все фото для номенклатуры $nomen удалены",
            path = request.requestURI
        )
    }

    // ==================== Вспомогательные приватные методы ====================

    private fun handleDownload(
        result: DownloadResult,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        when (result) {
            is DownloadResult.NotFound -> sendErrorResponse(response, result.message, request.requestURI)
            is DownloadResult.EmptyData -> sendErrorResponse(response, result.message, request.requestURI)
            is DownloadResult.Success -> {
                response.contentType = MediaType.IMAGE_JPEG_VALUE
                response.setHeader("Content-Disposition", "attachment; filename=\"${result.filename}\"")
                response.outputStream.write(result.data)
                response.outputStream.flush()
            }
        }
    }

    private fun writeImageToResponse(response: HttpServletResponse, data: ByteArray, disposition: String) {
        response.contentType = MediaType.IMAGE_JPEG_VALUE
        response.setHeader("Content-Disposition", disposition)
        response.outputStream.write(data)
        response.outputStream.flush()
    }

    private fun sendErrorResponse(response: HttpServletResponse, message: String, path: String) {
        response.status = HttpStatus.NOT_FOUND.value()
        response.contentType = "application/json;charset=UTF-8"
        val errorResponse = MyApiResponse.unsuccess<Nothing>(message = message, path = path)
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}