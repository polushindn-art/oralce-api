package com.example.oracleapi.service.barcode

import com.google.zxing.*
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.common.HybridBinarizer
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

@Service
class BarcodeService(
    private val restTemplate: RestTemplate
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    init {
        // ✅ Принудительно регистрируем плагины TwelveMonkeys
        try {
            ImageIO.scanForPlugins()
            log.info("✅ ImageIO плагины зарегистрированы")
        } catch (e: Exception) {
            log.warn("⚠️ Не удалось зарегистрировать плагины: ${e.message}")
        }
    }

    fun decodeBarcodeFromUrlWithAuth(imageUrl: String, token: String): String? {
        return try {
            val image = downloadImageWithAuth(imageUrl, token)
            if (image == null) {
                log.warn("⚠️ Не удалось загрузить изображение")
                return null
            }

            // ✅ Уменьшаем изображение
            val resizedImage = resizeImage(image, 1000, 1000)

            // ✅ Пробуем распознать на уменьшенном
            var result = tryDecode(resizedImage)
            if (result != null) {
                log.info("✅ Распознан код: ${result.text}, тип: ${result.barcodeFormat}")
                return result.text
            }

            // ✅ Если не нашли, пробуем с оригиналом
            result = tryDecode(image)
            if (result != null) {
                log.info("✅ Распознан код (оригинал): ${result.text}, тип: ${result.barcodeFormat}")
                return result.text
            }

            log.warn("⚠️ Код не найден на изображении")
            null
        } catch (e: Exception) {
            log.error("❌ Ошибка распознавания", e)
            null
        }
    }

    private fun tryDecode(image: BufferedImage): Result? {
        return try {
            val source = BufferedImageLuminanceSource(image)
            val bitmap = BinaryBitmap(HybridBinarizer(source))
            val reader = MultiFormatReader()

            val hints = mapOf(
                DecodeHintType.POSSIBLE_FORMATS to listOf(
                    BarcodeFormat.EAN_13,
                    BarcodeFormat.EAN_8,
                    BarcodeFormat.UPC_A,
                    BarcodeFormat.UPC_E,
                    BarcodeFormat.CODE_39,
                    BarcodeFormat.CODE_93,
                    BarcodeFormat.CODE_128,
                    BarcodeFormat.ITF,
                    BarcodeFormat.CODABAR,
                    BarcodeFormat.RSS_14,
                    BarcodeFormat.RSS_EXPANDED,
                    BarcodeFormat.QR_CODE,
                    BarcodeFormat.DATA_MATRIX,
                    BarcodeFormat.PDF_417,
                    BarcodeFormat.AZTEC
                ),
                DecodeHintType.TRY_HARDER to true
            )

            reader.decode(bitmap, hints)
        } catch (e: NotFoundException) {
            null
        } catch (e: Exception) {
            log.error("Ошибка декодирования", e)
            null
        }
    }

    private fun resizeImage(original: BufferedImage, maxWidth: Int, maxHeight: Int): BufferedImage {
        val width = original.width
        val height = original.height

        if (width <= maxWidth && height <= maxHeight) {
            return original
        }

        val ratio = minOf(maxWidth.toDouble() / width, maxHeight.toDouble() / height)
        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()

        val resized = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB)
        val g = resized.createGraphics()
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        g.drawImage(original, 0, 0, newWidth, newHeight, null)
        g.dispose()

        return resized
    }

    fun downloadImageWithAuth(url: String, token: String): BufferedImage? {
        return try {
            log.info("📥 [Barcode] Скачиваем изображение: $url")

            val headers = HttpHeaders().apply {
                set("Authorization", token)
                set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                set("Accept", "image/*")  // ✅ Просим изображение
            }

            val response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                HttpEntity<Nothing>(headers),
                ByteArray::class.java
            )

            val imageBytes = response.body
            if (imageBytes == null || imageBytes.isEmpty()) {
                log.warn("⚠️ Пустой ответ от сервера")
                return null
            }

            log.info("📥 [Barcode] Загружено ${imageBytes.size} байт")
            log.info("📥 [Barcode] Content-Type: ${response.headers.contentType}")

            // ✅ Пробуем прочитать через ImageIO
            var image = ImageIO.read(ByteArrayInputStream(imageBytes))
            if (image != null) {
                log.info("📥 [Barcode] Изображение прочитано через ImageIO")
                return image
            }

            // ✅ Если не получилось — пробуем через TwelveMonkeys
            log.info("📥 [Barcode] Пробуем через TwelveMonkeys...")

            val inputStream = ByteArrayInputStream(imageBytes)
            val imageInputStream = ImageIO.createImageInputStream(inputStream)
            val readers = ImageIO.getImageReaders(imageInputStream)

            if (readers.hasNext()) {
                val reader = readers.next()
                reader.setInput(imageInputStream)
                image = reader.read(0)
                log.info("📥 [Barcode] Изображение прочитано через reader: ${reader.formatName}")
                return image
            }

            log.warn("⚠️ Не удалось декодировать изображение")
            null
        } catch (e: Exception) {
            log.error("❌ Ошибка загрузки: ${e.message}")
            null
        }
    }
}