package com.example.oracleapi.service.picture

import com.example.oracleapi.dto.picture.PictureMetadata
import com.example.oracleapi.repository.picture.PictureRepository
import org.springframework.stereotype.Service

@Service
class PictureService(
    private val pictureRepository: PictureRepository
) {
    // Внутренний метод (если нужен другим сервисам)
    fun getPictureByRn(rn: Long) = pictureRepository.findById(rn).orElse(null)

    // Получение метаданных в виде DTO
    fun getMetadata(rn: Long): PictureMetadata? {
        val picture = pictureRepository.findById(rn).orElse(null) ?: return null
        return PictureMetadata.fromEntity(picture)
    }

    // Безопасное получение байтов файла или превью без утечки Entity наружу
    fun getPictureFile(rn: Long, isPreview: Boolean = false): PictureResult {
        val picture = pictureRepository.findById(rn).orElse(null)
            ?: return PictureResult.NotFound("Изображение с RN=$rn не найдено")

        val data = if (isPreview) picture.preview else picture.picture
        if (data == null || data.isEmpty()) {
            val msg = if (isPreview) "Миниатюра для изображения с RN=$rn не найдена" else "Изображение с RN=$rn не содержит данных"
            return PictureResult.NotFound(msg)
        }

        return PictureResult.Success(data, picture.datatype)
    }

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