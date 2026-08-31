package com.example.oracleapi.service.picture

import com.example.oracleapi.dto.picture.PictureMetadataDto
import com.example.oracleapi.repository.picture.PictureRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PictureService(
    private val pictureRepository: PictureRepository
) {
    // Внутренний метод (если нужен другим сервисам)
    @Transactional(readOnly = true)
    fun getPictureByRn(rn: Long) = pictureRepository.findById(rn).orElse(null)

    // Получение метаданных в виде DTO
    @Transactional(readOnly = true)
    fun getMetadata(rn: Long): PictureMetadataDto? {
        val picture = pictureRepository.findById(rn).orElse(null) ?: return null
        return PictureMetadataDto.fromEntity(picture)
    }

    // Безопасное получение байтов файла или превью без утечки Entity наружу
    @Transactional(readOnly = true)
    fun getPictureFile(rn: Long, isPreview: Boolean = false): PictureResult {
        val picture = pictureRepository.findByIdOrNull(rn)
            ?: return PictureResult.NotFound("Изображение с RN=$rn не найдено")

        val data = if (isPreview) picture.preview else picture.picture
        if (data == null || data.isEmpty()) {
            val msg = if (isPreview) "Миниатюра для изображения с RN=$rn не найдена" else "Изображение с RN=$rn не содержит данных"
            return PictureResult.NotFound(msg)
        }

        return PictureResult.Success(data, picture.datatype)
    }

    @Transactional(readOnly = true)
    fun getRnListByTablernNotDeleted(tablern: Long): List<Long> {
        return pictureRepository.findRnByTablern(tablern)
            .filter { it.deleted == null }
            .map { it.rn }
    }
}

sealed class PictureResult {
    data class Success(val data: ByteArray, val datatype: String) : PictureResult()
    data class NotFound(val message: String) : PictureResult()
}