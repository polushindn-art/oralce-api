package com.example.oracleapi.service

import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import javax.imageio.ImageReader
import java.util.*

@Component
class ImageService {

    /**
     * Получение информации об изображении
     * @return Triple<width, height, format> или null если не удалось прочитать
     */
    fun getImageInfo(data: ByteArray): ImageInfo? {
        return try {
            val inputStream = ByteArrayInputStream(data)
            val readers: Iterator<ImageReader> = ImageIO.getImageReaders(inputStream)

            if (!readers.hasNext()) {
                val detectedType = detectImageTypeByMagicBytes(data)
                return if (detectedType != null) {
                    ImageInfo(0, 0, detectedType)
                } else null
            }

            val reader = readers.next()
            reader.setInput(inputStream)

            val width = reader.getWidth(0)
            val height = reader.getHeight(0)
            val formatName = reader.formatName

            reader.dispose()

            ImageInfo(width, height, formatName)
        } catch (e: Exception) {
            println("Error reading image: ${e.message}")
            null
        }
    }

    /**
     * Проверка, является ли файл изображением
     */
    fun isValidImage(data: ByteArray): Boolean {
        val magicType = detectImageTypeByMagicBytes(data) ?: return false

        return try {
            val inputStream = ByteArrayInputStream(data)
            val bufferedImage = ImageIO.read(inputStream)
            bufferedImage != null || true // если магические байты верные, считаем валидным
        } catch (e: Exception) {
            true
        }
    }

    /**
     * Проверка файла MultipartFile
     */
    fun isValidImage(file: MultipartFile): Boolean {
        return isValidImage(file.bytes)
    }

    /**
     * Получение расширения файла
     */
    fun getFileExtension(file: MultipartFile): String {
        val originalFilename = file.originalFilename ?: "unknown.jpg"
        return originalFilename.substringAfterLast(".", "jpg").lowercase()
    }

    /**
     * Проверка поддерживаемого формата
     */
    fun isSupportedFormat(extension: String): Boolean {
        val supportedFormats = listOf("jpg", "jpeg", "png", "gif", "webp", "bmp")
        return extension in supportedFormats
    }

    /**
     * Создание превью изображения
     * @param data оригинальное изображение в байтах
     * @param maxWidth максимальная ширина (по умолчанию 300)
     * @param maxHeight максимальная высота (по умолчанию 300)
     * @param format формат выходного изображения (jpg, png)
     * @param minSize минимальный размер файла для создания превью (байты)
     */
    fun generatePreview(
        data: ByteArray,
        maxWidth: Int = 300,
        maxHeight: Int = 300,
        format: String = "jpg",
        minSize: Int = 50_000
    ): ByteArray? {
        // Для маленьких файлов превью не создаем
        if (data.size < minSize) {
            return null
        }

        return try {
            val inputStream = ByteArrayInputStream(data)
            val originalImage = ImageIO.read(inputStream)

            if (originalImage == null) {
                println("Cannot read original image for preview")
                return null
            }

            val originalWidth = originalImage.width
            val originalHeight = originalImage.height

            // Вычисляем новые размеры с сохранением пропорций
            val scale = minOf(
                maxWidth.toDouble() / originalWidth,
                maxHeight.toDouble() / originalHeight
            )
            val scaledWidth = (originalWidth * scale).toInt()
            val scaledHeight = (originalHeight * scale).toInt()

            if (scaledWidth <= 0 || scaledHeight <= 0) {
                return null
            }

            // Создаем масштабированное изображение
            val scaledImage = originalImage.getScaledInstance(
                scaledWidth, scaledHeight, Image.SCALE_SMOOTH
            )
            val bufferedScaled = BufferedImage(
                scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB
            )
            val graphics = bufferedScaled.createGraphics()
            graphics.drawImage(scaledImage, 0, 0, null)
            graphics.dispose()

            // Конвертируем в байты
            val outputStream = ByteArrayOutputStream()
            val outputFormat = when (format.lowercase()) {
                "png" -> "png"
                "gif" -> "gif"
                else -> "jpg"
            }

            ImageIO.write(bufferedScaled, outputFormat, outputStream)
            outputStream.toByteArray()

        } catch (e: Exception) {
            println("Error generating preview: ${e.message}")
            null
        }
    }

    /**
     * Генерация превью из MultipartFile
     */
    fun generatePreview(file: MultipartFile, maxWidth: Int = 300, maxHeight: Int = 300): ByteArray? {
        return generatePreview(file.bytes, maxWidth, maxHeight)
    }

    /**
     * Определение типа изображения по магическим байтам (заголовку файла)
     */
    private fun detectImageTypeByMagicBytes(data: ByteArray): String? {
        return when {
            data.size < 4 -> null
            // JPEG
            data[0] == 0xFF.toByte() && data[1] == 0xD8.toByte() -> "jpeg"
            // PNG
            data[0] == 0x89.toByte() && data[1] == 0x50.toByte() &&
                    data[2] == 0x4E.toByte() && data[3] == 0x47.toByte() -> "png"
            // GIF
            data[0] == 0x47.toByte() && data[1] == 0x49.toByte() && data[2] == 0x46.toByte() -> "gif"
            // BMP
            data[0] == 0x42.toByte() && data[1] == 0x4D.toByte() -> "bmp"
            // WebP
            data[0] == 0x52.toByte() && data[1] == 0x49.toByte() &&
                    data[2] == 0x46.toByte() && data[3] == 0x46.toByte() -> "webp"
            else -> null
        }
    }
}

/**
 * Класс с информацией об изображении
 */
data class ImageInfo(
    val width: Int,
    val height: Int,
    val format: String
)