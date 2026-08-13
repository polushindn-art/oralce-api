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
import java.awt.*
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

@Service
class BarcodeService(
    private val restTemplate: RestTemplate
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    init {
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

            // ✅ Только 2 попытки: оригинал + увеличенный контраст
            var result = tryDecode(image)
            if (result != null) {
                log.info("✅ Распознан код: ${result.text}")
                return result.text
            }

            // Если не распозналось — пробуем с увеличенным контрастом
            val enhanced = enhanceImage(image)
            result = tryDecode(enhanced)
            if (result != null) {
                log.info("✅ Распознан код (улучшенный): ${result.text}")
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
            // Конвертируем в градации серого для лучшего распознавания
            val grayImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_BYTE_GRAY)
            val g = grayImage.createGraphics()
            g.drawImage(image, 0, 0, null)
            g.dispose()

            val source = BufferedImageLuminanceSource(grayImage)
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
                DecodeHintType.TRY_HARDER to true,
                DecodeHintType.PURE_BARCODE to true
            )

            reader.decode(bitmap, hints)
        } catch (e: NotFoundException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Быстрое улучшение изображения (контраст + резкость)
     */
    private fun enhanceImage(image: BufferedImage): BufferedImage {
        val result = BufferedImage(image.width, image.height, image.type)
        val g = result.createGraphics()
        g.drawImage(image, 0, 0, null)
        g.dispose()

        // Увеличиваем контраст
        for (x in 0 until result.width) {
            for (y in 0 until result.height) {
                val rgb = result.getRGB(x, y)
                val r = ((rgb shr 16 and 0xFF) * 1.3).coerceIn(0.0, 255.0).toInt()
                val g1 = ((rgb shr 8 and 0xFF) * 1.3).coerceIn(0.0, 255.0).toInt()
                val b = ((rgb and 0xFF) * 1.3).coerceIn(0.0, 255.0).toInt()
                result.setRGB(x, y, Color(r, g1, b).rgb)
            }
        }

        return result
    }

    fun downloadImageWithAuth(url: String, token: String): BufferedImage? {
        return try {
            log.info("📥 [Barcode] Скачиваем изображение: $url")

            val headers = HttpHeaders().apply {
                set("Authorization", token)
                set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                set("Accept", "image/*")
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

            var image = ImageIO.read(ByteArrayInputStream(imageBytes))
            if (image != null) {
                log.info("📥 [Barcode] Изображение прочитано через ImageIO")
                return image
            }

            // Пробуем через TwelveMonkeys
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