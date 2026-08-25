package com.example.oracleapi.service.nomnlistdata

import com.example.oracleapi.config.UserDetailsFromToken
import com.example.oracleapi.dto.nomnlistdata.NomnlistdataMetadata
import com.example.oracleapi.entity.nomnlistdata.Nomnlistdata
import com.example.oracleapi.repository.nomnlistdata.NomnlistdataRepository
import com.example.oracleapi.service.ImageService
import com.example.oracleapi.service.public.PublicGenIdRnProcedur
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@Service
class NomnlistdataService(
    private val repository: NomnlistdataRepository,
    private val genIdRnProcedur: PublicGenIdRnProcedur,
    private val imageService: ImageService
) {

    private fun getCurrentUserAgn(): Long? {
        val authentication = SecurityContextHolder.getContext().authentication
        val userDetails = authentication?.principal as? UserDetailsFromToken
        return userDetails?.userAgn
    }

    // ========== МЕТОДЫ ДЛЯ INFO (Возвращают DTO) ==========

    fun getInfoByNomen(nomen: Long): List<NomnlistdataMetadata> {
        return repository.findByNomenNative(nomen).map { NomnlistdataMetadata.fromEntity(it) }
    }

    fun getPhotoInfoByNomen(nomen: Long, photonum: Int): NomnlistdataMetadata? {
        val photo = repository.findByNomenAndPhotonumNative(nomen, photonum) ?: return null
        return NomnlistdataMetadata.fromEntity(photo)
    }

    // ========== МЕТОДЫ ДЛЯ ПРОСМОТРА И СКАЧИВАНИЯ БАЙТОВ ==========

    fun getPhotoData(nomen: Long, photonum: Int, isPreview: Boolean = false): ByteArray? {
        val photo = repository.findByNomenAndPhotonumNative(nomen, photonum) ?: return null
        val data = if (isPreview) photo.minidata else photo.imageData
        if (data == null || data.isEmpty()) return null
        return data
    }

    fun getMainPhotoData(nomen: Long): ByteArray? {
        val photo = repository.findByNomenAndPhotonumNative(nomen, 1) ?: return null
        val data = photo.imageData
        if (data == null || data.isEmpty()) return null
        return data
    }

    fun getFirstPhotoData(nomen: Long): ByteArray? {
        val photo = repository.findByNomenNative(nomen).firstOrNull() ?: return null
        val data = photo.imageData
        if (data == null || data.isEmpty()) return null
        return data
    }

    fun getPhotoDownloadData(nomen: Long, photonum: Int, isPreview: Boolean = false): DownloadResult {
        val photo = repository.findByNomenAndPhotonumNative(nomen, photonum)
            ?: return DownloadResult.NotFound("Фото для номенклатуры $nomen (№$photonum) не найдено")

        val data = if (isPreview) photo.minidata else photo.imageData
        if (data == null || data.isEmpty()) {
            return DownloadResult.EmptyData(if (isPreview) "Превью для фото не найдено" else "Фото не содержит данных")
        }

        val prefix = if (isPreview) "preview" else "photo"
        val filename = "${prefix}_${nomen}_${photonum}.jpg"
        return DownloadResult.Success(data, filename)
    }

    // ========== ЗАГРУЗКА (Возвращает DTO) ==========

    @Transactional
    fun uploadPhoto(
        nomen: Long,
        file: MultipartFile,
        needDownload: Boolean? = null
    ): NomnlistdataMetadata {
        if (file.isEmpty) {
            throw IllegalArgumentException("Файл не может быть пустым")
        }

        val data = file.bytes
        val extension = imageService.getFileExtension(file)
        val manager = getCurrentUserAgn()

        if (!imageService.isSupportedFormat(extension)) {
            val supported = listOf("jpg", "jpeg", "png", "gif", "webp", "bmp")
            throw IllegalArgumentException("Неподдерживаемый формат: $extension. Поддерживаются: ${supported.joinToString()}")
        }

        if (!imageService.isValidImage(file)) {
            throw IllegalArgumentException("Файл не является корректным изображением")
        }

        val imageInfo = imageService.getImageInfo(data)
        val (width, height) = if (imageInfo != null && imageInfo.width > 0) {
            Pair(imageInfo.width, imageInfo.height)
        } else {
            Pair(0, 0)
        }

        val maxPhotonum = repository.findMaxPhotonumByNomenNative(nomen) ?: 0
        val newPhotonum = maxPhotonum + 1
        val size = file.size
        val preview = imageService.generatePreview(data)
        val newRn = genIdRnProcedur.take().rn

        repository.insertNative(
            rn = newRn,
            nomen = nomen,
            data = data,
            photonum = newPhotonum,
            needdownload = if (needDownload == true) 1 else 0,
            manager = manager,
            created = LocalDateTime.now(),
            deleted = null,
            deluser = null,
            md5 = null,
            minidata = preview,
            height = height,
            width = width,
            size = size
        )

        val createdPhoto = repository.findByNomenAndPhotonumNative(nomen, newPhotonum)!!
        return NomnlistdataMetadata.fromEntity(createdPhoto)
    }

    // ========== УДАЛЕНИЕ ==========

    @Transactional
    fun softDeleteByNomenAndPhotonum(nomen: Long, photonum: Int): Boolean {
        val photo = repository.findByNomenAndPhotonumNative(nomen, photonum) ?: return false
        val currentUserAgn = getCurrentUserAgn()
        repository.softDeleteByRnNative(photo.rn, LocalDateTime.now(), currentUserAgn)
        return true
    }

    @Transactional
    fun softDeleteByNomen(nomen: Long) {
        val currentUserAgn = getCurrentUserAgn()
        repository.softDeleteByNomenNative(nomen, LocalDateTime.now(), currentUserAgn)
    }
}

sealed class DownloadResult {
    data class Success(val data: ByteArray, val filename: String) : DownloadResult()
    data class NotFound(val message: String) : DownloadResult()
    data class EmptyData(val message: String) : DownloadResult()
}