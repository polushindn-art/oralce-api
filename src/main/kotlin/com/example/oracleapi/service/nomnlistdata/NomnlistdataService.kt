package com.example.oracleapi.service.nomnlistdata

import com.example.oracleapi.config.UserDetailsFromToken
import com.example.oracleapi.entity.nomnlistdata.Nomnlistdata
import com.example.oracleapi.repository.nomnlistdata.NomnlistdataRepository
import com.example.oracleapi.repository.userlist.UserlistRepository
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

    /**
     * Получить userAgn из SecurityContext (из токена)
     */
    private fun getCurrentUserAgn(): Long? {
        val authentication = SecurityContextHolder.getContext().authentication
        val userDetails = authentication?.principal as? UserDetailsFromToken
        return userDetails?.userAgn
    }

    fun getByRn(rn: Long): Nomnlistdata? = repository.findByRnNative(rn)

    fun getByNomen(nomen: Long): List<Nomnlistdata> = repository.findByNomenNative(nomen)

    fun getByNomenAndPhotonum(nomen: Long, photonum: Int): Nomnlistdata? =
        repository.findByNomenAndPhotonumNative(nomen, photonum)

    fun getFirstByNomen(nomen: Long): Nomnlistdata? {
        return repository.findByNomenNative(nomen).firstOrNull()
    }

    fun getMainByNomen(nomen: Long): Nomnlistdata? {
        return repository.findByNomenAndPhotonumNative(nomen, 1)
    }

    fun getAllByNomen(nomen: Long): List<Nomnlistdata> = repository.findByNomenNative(nomen)

    fun getPhotoCount(nomen: Long): Long = repository.countByNomenNative(nomen)

    // ========== INSERT/UPDATE через NATIVE QUERIES ==========

    @Transactional
    fun uploadPhoto(
        nomen: Long,
        file: MultipartFile,
        needDownload: Boolean? = null
    ): Nomnlistdata {
        if (file.isEmpty) {
            throw IllegalArgumentException("Файл не может быть пустым")
        }

        val data = file.bytes
        val extension = imageService.getFileExtension(file)

        // Получаем manager из текущего пользователя
        val manager = getCurrentUserAgn()

        // Проверка формата
        if (!imageService.isSupportedFormat(extension)) {
            val supported = listOf("jpg", "jpeg", "png", "gif", "webp", "bmp")
            throw IllegalArgumentException("Неподдерживаемый формат: $extension. Поддерживаются: ${supported.joinToString()}")
        }

        // Валидация изображения
        if (!imageService.isValidImage(file)) {
            throw IllegalArgumentException("Файл не является корректным изображением")
        }

        // Получаем информацию об изображении
        val imageInfo = imageService.getImageInfo(data)
        val (width, height) = if (imageInfo != null && imageInfo.width > 0) {
            Pair(imageInfo.width, imageInfo.height)
        } else {
            Pair(0, 0)
        }

        // Получаем максимальный номер фото
        val maxPhotonum = repository.findMaxPhotonumByNomenNative(nomen) ?: 0
        val newPhotonum = maxPhotonum + 1

        println("Adding new photo for nomen=$nomen, new photonum=$newPhotonum, size=${width}x${height}")

        val size = file.size

        // Генерируем превью через ImageService
        val preview = imageService.generatePreview(data)

        // Генерируем RN
        val newRn = genIdRnProcedur.generateRn().rn


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

        return repository.findByNomenAndPhotonumNative(nomen, newPhotonum)!!
    }

    // ========== DELETE ==========

    @Transactional
    fun softDelete(rn: Long) {
        val currentUserAgn = getCurrentUserAgn()
        repository.softDeleteByRnNative(rn, LocalDateTime.now(), currentUserAgn)
    }

    @Transactional
    fun softDeleteByNomen(nomen: Long) {
        val currentUserAgn = getCurrentUserAgn()
        repository.softDeleteByNomenNative(nomen, LocalDateTime.now(), currentUserAgn)
    }

    @Transactional
    fun hardDelete(rn: Long) {
        repository.hardDeleteByRnNative(rn)
    }
}