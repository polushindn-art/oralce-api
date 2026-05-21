package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.nomnlistdata.NomnlistdataMetadata
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

    // ==================== INFO (получение информации) ====================

    @GetMapping("/by-nomen/{nomen}/info")
    @Operation(summary = "Получить список всех фото для номенклатуры")
    fun getInfoByNomen(
        @PathVariable nomen: Long
    ): MyApiResponse<List<NomnlistdataMetadata>> {
        val photos = service.getByNomen(nomen)
        val metadata = photos.map { NomnlistdataMetadata.fromEntity(it) }
        return successList(metadata)
    }

    @GetMapping("/by-nomen/{nomen}/info/{photonum}")
    @Operation(summary = "Получить информацию о конкретном фото номенклатуры")
    fun getPhotoInfoByNomen(
        @PathVariable nomen: Long,
        @PathVariable photonum: Int
    ): MyApiResponse<NomnlistdataMetadata> {
        val photo = service.getByNomenAndPhotonum(nomen, photonum)
            ?: return error(
                message = "Фото для номенклатуры $nomen (№$photonum) не найдено"
            )
        return success(
            NomnlistdataMetadata.fromEntity(photo)
        )
    }

    // ==================== VIEW (просмотр) ====================

    @GetMapping("/by-nomen/{nomen}/view/{photonum}")
    @Operation(summary = "Просмотр конкретного фото номенклатуры")
    fun viewByNomenAndPhotonum(
        @PathVariable nomen: Long,
        @PathVariable photonum: Int,
        response: HttpServletResponse
    ) {
        val photo = service.getByNomenAndPhotonum(nomen, photonum)

        if (photo == null) {
            sendErrorResponse(
                response,
                "Фото для номенклатуры $nomen (№$photonum) не найдено",
                "/nomen-photos/by-nomen/$nomen/view/$photonum"
            )
            return
        }

        val data = photo.imageData
        if (data == null || data.isEmpty()) {
            sendErrorResponse(response, "Фото не содержит данных", "/nomen-photos/by-nomen/$nomen/view/$photonum")
            return
        }

        response.contentType = MediaType.IMAGE_JPEG_VALUE
        response.setHeader("Content-Disposition", "inline")
        response.outputStream.write(data)
        response.outputStream.flush()
    }

    @GetMapping("/by-nomen/{nomen}/main")
    @Operation(summary = "Просмотр главного фото номенклатуры (photonum=1)")
    fun viewMainByNomen(
        @PathVariable nomen: Long,
        response: HttpServletResponse
    ) {
        val photo = service.getMainByNomen(nomen)

        if (photo == null) {
            sendErrorResponse(
                response,
                "Главное фото для номенклатуры $nomen не найдено",
                "/nomen-photos/by-nomen/$nomen/main"
            )
            return
        }

        val data = photo.imageData
        if (data == null || data.isEmpty()) {
            sendErrorResponse(response, "Фото не содержит данных", "/nomen-photos/by-nomen/$nomen/main")
            return
        }

        response.contentType = MediaType.IMAGE_JPEG_VALUE
        response.setHeader("Content-Disposition", "inline")
        response.outputStream.write(data)
        response.outputStream.flush()
    }

    @GetMapping("/by-nomen/{nomen}/view")
    @Operation(summary = "Просмотр первого фото номенклатуры (photonum=1)")
    fun viewFirstByNomen(
        @PathVariable nomen: Long,
        response: HttpServletResponse
    ) {
        val photo = service.getFirstByNomen(nomen)

        if (photo == null) {
            sendErrorResponse(response, "Фото для номенклатуры $nomen не найдено", "/nomen-photos/by-nomen/$nomen/view")
            return
        }

        val data = photo.imageData
        if (data == null || data.isEmpty()) {
            sendErrorResponse(response, "Фото не содержит данных", "/nomen-photos/by-nomen/$nomen/view")
            return
        }

        response.contentType = MediaType.IMAGE_JPEG_VALUE
        response.setHeader("Content-Disposition", "inline")
        response.outputStream.write(data)
        response.outputStream.flush()
    }

    // ==================== DOWNLOAD (скачивание) ====================

    @GetMapping("/by-nomen/{nomen}/download/{photonum}")
    @Operation(summary = "Скачать конкретное фото номенклатуры")
    fun downloadByNomenAndPhotonum(
        @PathVariable nomen: Long,
        @PathVariable photonum: Int,
        response: HttpServletResponse
    ) {
        val photo = service.getByNomenAndPhotonum(nomen, photonum)

        if (photo == null) {
            sendErrorResponse(
                response,
                "Фото для номенклатуры $nomen (№$photonum) не найдено",
                "/nomen-photos/by-nomen/$nomen/download/$photonum"
            )
            return
        }

        val data = photo.imageData
        if (data == null || data.isEmpty()) {
            sendErrorResponse(response, "Фото не содержит данных", "/nomen-photos/by-nomen/$nomen/download/$photonum")
            return
        }

        val filename = "photo_${nomen}_${photonum}.jpg"
        response.contentType = MediaType.IMAGE_JPEG_VALUE
        response.setHeader("Content-Disposition", "attachment; filename=\"$filename\"")
        response.outputStream.write(data)
        response.outputStream.flush()
    }

    // ==================== PREVIEW (превью) ====================

    @GetMapping("/by-nomen/{nomen}/preview/{photonum}")
    @Operation(summary = "Просмотр превью конкретного фото номенклатуры")
    fun previewByNomenAndPhotonum(
        @PathVariable nomen: Long,
        @PathVariable photonum: Int,
        response: HttpServletResponse
    ) {
        val photo = service.getByNomenAndPhotonum(nomen, photonum)

        if (photo == null) {
            sendErrorResponse(
                response,
                "Фото для номенклатуры $nomen (№$photonum) не найдено",
                "/nomen-photos/by-nomen/$nomen/preview/$photonum"
            )
            return
        }

        val preview = photo.minidata
        if (preview == null || preview.isEmpty()) {
            sendErrorResponse(response, "Превью для фото не найдено", "/nomen-photos/by-nomen/$nomen/preview/$photonum")
            return
        }

        response.contentType = MediaType.IMAGE_JPEG_VALUE
        response.setHeader("Content-Disposition", "inline")
        response.outputStream.write(preview)
        response.outputStream.flush()
    }

    @GetMapping("/by-nomen/{nomen}/preview/download/{photonum}")
    @Operation(summary = "Скачать превью конкретного фото номенклатуры")
    fun downloadPreviewByNomenAndPhotonum(
        @PathVariable nomen: Long,
        @PathVariable photonum: Int,
        response: HttpServletResponse
    ) {
        val photo = service.getByNomenAndPhotonum(nomen, photonum)

        if (photo == null) {
            sendErrorResponse(
                response,
                "Фото для номенклатуры $nomen (№$photonum) не найдено",
                "/nomen-photos/by-nomen/$nomen/preview/download/$photonum"
            )
            return
        }

        val preview = photo.minidata
        if (preview == null || preview.isEmpty()) {
            sendErrorResponse(
                response,
                "Превью для фото не найдено",
                "/nomen-photos/by-nomen/$nomen/preview/download/$photonum"
            )
            return
        }

        val filename = "preview_${nomen}_${photonum}.jpg"
        response.contentType = MediaType.IMAGE_JPEG_VALUE
        response.setHeader("Content-Disposition", "attachment; filename=\"$filename\"")
        response.outputStream.write(preview)
        response.outputStream.flush()
    }

    // ==================== CRUD (создание, удаление) ====================

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "Загрузить новое фото для номенклатуры")
    fun upload(
        @RequestParam nomen: Long,
        @RequestParam file: MultipartFile,
        @RequestParam(required = false) needDownload: Boolean?
    ): MyApiResponse<NomnlistdataMetadata> {
        return try {
            val photo = service.uploadPhoto(
                nomen = nomen,
                file = file,
                needDownload = needDownload
            )
            success(NomnlistdataMetadata.fromEntity(photo))
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
        val photo = service.getByNomenAndPhotonum(nomen, photonum) ?: return MyApiResponse.unsuccess(
            message = "Фото для номенклатуры $nomen (№$photonum) не найдено",
            path = request.requestURI
        )
        service.softDelete(photo.rn)
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

    // ==================== Вспомогательные методы ====================

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
}