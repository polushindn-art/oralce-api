package com.example.oracleapi.service.barcode

import com.google.zxing.*
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import nu.pattern.OpenCV
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

data class DecodeDetailResult(
    val text: String,
    val format: BarcodeFormat,
    val preprocessingMethod: String,
    val angle: Int
)

// Вспомогательная функция для безопасного управления памятью (C++) OpenCV
inline fun <T> Mat.use(block: (Mat) -> T): T {
    try {
        return block(this)
    } finally {
        this.release()
    }
}

@Service
class BarcodeService(
    private val restTemplate: RestTemplate
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    private var isOpenCvLoaded = false

    init {
        try {
            OpenCV.loadLocally()
            isOpenCvLoaded = true
            log.info("✅ OpenCV успешно загружен и инициализирован!")
        } catch (e: Throwable) {
            log.warn("⚠️ Не удалось загрузить OpenCV, включен резервный режим: ${e.message}")
        }
    }

    private val decodeHints = mapOf(
        // TRY_HARDER убран, так как он сканирует вертикально, а мы и так вращаем изображение вручную.
        // Без него код работает быстрее без потери эффективности.
        DecodeHintType.TRY_HARDER to true,
        DecodeHintType.CHARACTER_SET to "UTF-8",
        DecodeHintType.POSSIBLE_FORMATS to listOf(
            BarcodeFormat.EAN_13, BarcodeFormat.EAN_8, BarcodeFormat.UPC_A,
            BarcodeFormat.CODE_128, BarcodeFormat.CODE_39, BarcodeFormat.CODE_93, BarcodeFormat.ITF,
            BarcodeFormat.QR_CODE, BarcodeFormat.DATA_MATRIX, BarcodeFormat.PDF_417
        )
    )

    // Функция теперь suspend, не блокирует поток контейнера (Tomcat/Netty)
    suspend fun decodeBarcodeFromUrlWithAuth(imageUrl: String, token: String): String? = coroutineScope {
        try {
            val startTime = System.currentTimeMillis()

            val originalImage = downloadImageWithAuth(imageUrl, token) ?: run {
                log.warn("⚠️ Не удалось загрузить изображение: $imageUrl")
                return@coroutineScope null
            }

            val baseImage = scaleDownIfNeeded(originalImage, maxDimension = 1920)

            // Параллельный запуск анализа по 4 углам
            val result = raceDecodeAngles(baseImage)

            if (result != null) {
                val elapsedTime = System.currentTimeMillis() - startTime
                log.info(
                    "🎯 [УСПЕХ за ${elapsedTime}мс] Текст: '{}' | Поворот: {}° | Метод: '{}' | Формат: {}",
                    result.text,
                    result.angle,
                    result.preprocessingMethod,
                    result.format
                )
                return@coroutineScope result.text
            }

            log.warn("⚠️ Штрих-код не найден ни одним алгоритмом за ${System.currentTimeMillis() - startTime} мс")
            null
        } catch (e: Exception) {
            log.error("❌ Ошибка распознавания", e)
            null
        }
    }

    private suspend fun raceDecodeAngles(baseImage: BufferedImage): DecodeDetailResult? = coroutineScope {
        val channel = Channel<DecodeDetailResult?>(capacity = Channel.BUFFERED)
        val angles = listOf(0, 90, 180, 270)

        val jobs = angles.map { angle ->
            launch(Dispatchers.Default) {
                try {
                    val rotated = if (angle == 0) baseImage else rotateImage(baseImage, angle)
                    val found = tryDecodeWithOpenCvAndFilters(rotated, angle)
                    if (found != null) {
                        channel.trySend(found)
                    }
                } catch (e: CancellationException) {
                    // Корутина была отменена, потому что другой угол уже нашел штрих-код - это нормально
                    throw e
                } catch (e: Exception) {
                    log.error("Ошибка при обработке угла $angle", e)
                }
            }
        }

        // Следим за завершением всех джобов. Если все отработали и ничего не нашли - закрываем канал
        launch {
            jobs.joinAll()
            channel.close()
        }

        // Ждем первого пришедшего результата. Если канал закроется пустым, вернет null
        val winner = channel.receiveCatching().getOrNull()

        // Отменяем все остальные вычисления, так как победитель найден (или все завершились)
        coroutineContext.cancelChildren()

        winner
    }

    private fun tryDecodeWithOpenCvAndFilters(image: BufferedImage, angle: Int): DecodeDetailResult? {
        val reader = MultiFormatReader().apply { setHints(decodeHints) }

        // 1. Исходник + Нарезка
        tryDecodeSlices(image, "Оригинал", angle, reader)?.let { return it}

        // 2. Красный канал + Нарезка
        val redChannelImage = extractRedChannel(image)
        tryDecodeSlices(redChannelImage, "Красный канал", angle, reader)?.let { return it }

        if (isOpenCvLoaded) {
            try {
                // Превращаем картинку в серую матрицу для OpenCV
                bufferedImageToMat(image).use { mat ->

                    // УРОВЕНЬ А: CLAHE (Вытягивает контраст на металле и пластике)
                    Mat().use { claheMat ->
                        val clahe = Imgproc.createCLAHE(3.0, Size(8.0, 8.0))
                        clahe.apply(mat, claheMat)

                        // Пробуем CLAHE с нарезкой
                        tryDecodeSlices(matToBufferedImage(claheMat), "OpenCV CLAHE", angle, reader)?.let { return it }

                        // УРОВЕНЬ Б: Порог Оцу (Otsu) — идеальное разделение черного и белого
                        Mat().use { otsuMat ->
                            Imgproc.threshold(claheMat, otsuMat, 0.0, 255.0, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU)
                            tryDecodeSlices(matToBufferedImage(otsuMat), "OpenCV Otsu Binary", angle, reader)?.let { return it }
                        }
                    }

                    // УРОВЕНЬ В: Поиск области штрих-кода (Crop)
                    opencvFindAndCropBarcodeRegion(mat)?.let { croppedRegion ->
                        tryDecodeSlices(croppedRegion, "OpenCV Crop Region", angle, reader)?.let { return it }

                        // Если кроп нашелся, но мелкий — пробуем его увеличить в 2 раза (Zoom)
                        val zoomed = scaleUp(croppedRegion, 2.0)
                        tryDecodeSlices(zoomed, "OpenCV Crop + Zoom 2x", angle, reader)?.let { return it }
                    }
                }
            } catch (e: Exception) {
                log.debug("OpenCV processing skipped: ${e.message}")
            }
        }

        // Резервный кроп центра (60%) + Увеличение
        val centerCropped = cropCenter(image, 0.6)
        val zoomedCenter = scaleUp(centerCropped, 2.0)
        tryDecodeSlices(zoomedCenter, "Crop 60% + Zoom", angle, reader)?.let { return it }

        return null
    }

    /**
     * Поиск прямоугольной области штрих-кода с безопасным освобождением ВСЕХ Mat объектов.
     */
    private fun opencvFindAndCropBarcodeRegion(mat: Mat): BufferedImage? {
        val matsToRelease = mutableListOf<Mat>()
        fun createMat(): Mat = Mat().also { matsToRelease.add(it) }

        try {
            val gradX = createMat()
            val gradY = createMat()
            Imgproc.Sobel(mat, gradX, CvType.CV_32F, 1, 0, -1)
            Imgproc.Sobel(mat, gradY, CvType.CV_32F, 0, 1, -1)

            val gradient = createMat()
            Core.subtract(gradX, gradY, gradient)
            Core.convertScaleAbs(gradient, gradient)

            Imgproc.blur(gradient, gradient, Size(9.0, 9.0))
            Imgproc.threshold(gradient, gradient, 225.0, 255.0, Imgproc.THRESH_BINARY)

            val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(21.0, 7.0))
            matsToRelease.add(kernel)
            Imgproc.morphologyEx(gradient, gradient, Imgproc.MORPH_CLOSE, kernel)

            val contours = ArrayList<MatOfPoint>()
            val hierarchy = createMat()
            Imgproc.findContours(gradient, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)
            matsToRelease.addAll(contours) // Обязательно добавляем контуры к очистке

            var maxArea = 0.0
            var bestRect: Rect? = null

            for (contour in contours) {
                val rect = Imgproc.boundingRect(contour)
                val area = rect.width.toDouble() * rect.height
                val aspectRatio = rect.width.toDouble() / rect.height

                if (area > maxArea && area > 3000 && aspectRatio > 0.8) {
                    maxArea = area
                    bestRect = rect
                }
            }

            if (bestRect != null) {
                val padX = (bestRect.width * 0.1).toInt()
                val padY = (bestRect.height * 0.1).toInt()

                val x = maxOf(0, bestRect.x - padX)
                val y = maxOf(0, bestRect.y - padY)
                val w = minOf(mat.cols() - x, bestRect.width + padX * 2)
                val h = minOf(mat.rows() - y, bestRect.height + padY * 2)

                val croppedMat = Mat(mat, Rect(x, y, w, h))
                matsToRelease.add(croppedMat)
                return matToBufferedImage(croppedMat)
            }

            return null
        } finally {
            // Гарантированно освобождаем всю память C++
            matsToRelease.forEach { it.release() }
        }
    }

    private fun tryDecodeAllMethods(
        image: BufferedImage,
        filterName: String,
        angle: Int,
        reader: MultiFormatReader
    ): DecodeDetailResult? {
        val source = BufferedImageLuminanceSource(image)

        try {
            val res = reader.decodeWithState(BinaryBitmap(HybridBinarizer(source)))
            if (isValidBarcodeResult(res.text, res.barcodeFormat)) {
                return DecodeDetailResult(res.text, res.barcodeFormat, filterName, angle)
            }
        } catch (_: NotFoundException) {}
        finally { reader.reset() }

        try {
            val res = reader.decodeWithState(BinaryBitmap(GlobalHistogramBinarizer(source)))
            if (isValidBarcodeResult(res.text, res.barcodeFormat)) {
                return DecodeDetailResult(res.text, res.barcodeFormat, filterName, angle)
            }
        } catch (_: NotFoundException) {}
        finally { reader.reset() }

        try {
            val res = reader.decodeWithState(BinaryBitmap(HybridBinarizer(source.invert())))
            if (isValidBarcodeResult(res.text, res.barcodeFormat)) {
                return DecodeDetailResult(res.text, res.barcodeFormat, filterName, angle)
            }
        } catch (_: NotFoundException) {}
        finally { reader.reset() }

        return null
    }

    // ================= КОНВЕРТЕРЫ BUFFEREDIMAGE <-> OPENCV MAT =================

    private fun bufferedImageToMat(bi: BufferedImage): Mat {
        val gray = if (bi.type == BufferedImage.TYPE_BYTE_GRAY) bi else toGrayscale(bi)

        // Защита от ClassCastException
        require(gray.raster.dataBuffer is DataBufferByte) { "Изображение должно использовать DataBufferByte" }

        val data = (gray.raster.dataBuffer as DataBufferByte).data
        val mat = Mat(gray.height, gray.width, CvType.CV_8UC1)
        mat.put(0, 0, data)
        return mat // Вызывающая сторона обязана сделать .release()
    }

    private fun matToBufferedImage(mat: Mat): BufferedImage {
        val width = mat.cols()
        val height = mat.rows()
        val data = ByteArray(width * height)
        mat.get(0, 0, data)
        val image = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)
        image.raster.setDataElements(0, 0, width, height, data)
        return image
    }

    // ================= ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ =================

    private fun toGrayscale(image: BufferedImage): BufferedImage {
        val gray = BufferedImage(image.width, image.height, BufferedImage.TYPE_BYTE_GRAY)
        val g = gray.createGraphics()
        g.drawImage(image, 0, 0, null)
        g.dispose()
        return gray
    }

    private fun cropCenter(image: BufferedImage, cropPercent: Double): BufferedImage {
        val cropWidth = (image.width * cropPercent).toInt()
        val cropHeight = (image.height * cropPercent).toInt()
        val x = (image.width - cropWidth) / 2
        val y = (image.height - cropHeight) / 2
        return image.getSubimage(x, y, cropWidth, cropHeight)
    }

    private fun scaleDownIfNeeded(image: BufferedImage, maxDimension: Int): BufferedImage {
        val width = image.width
        val height = image.height
        if (width <= maxDimension && height <= maxDimension) return image

        val ratio = maxDimension.toDouble() / maxOf(width, height)
        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()

        val resized = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB)
        val g2d = resized.createGraphics()
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        g2d.drawImage(image, 0, 0, newWidth, newHeight, null)
        g2d.dispose()
        return resized
    }

    private fun rotateImage(image: BufferedImage, angle: Int): BufferedImage {
        val width = image.width
        val height = image.height
        val newWidth = if (angle == 90 || angle == 270) height else width
        val newHeight = if (angle == 90 || angle == 270) width else height

        val rotated = BufferedImage(newWidth, newHeight, image.type)
        val g2d = rotated.createGraphics()
        g2d.translate((newWidth - width) / 2, (newHeight - height) / 2)
        g2d.rotate(Math.toRadians(angle.toDouble()), width / 2.0, height / 2.0)
        g2d.drawImage(image, 0, 0, null)
        g2d.dispose()
        return rotated
    }

    // Сетевой вызов изолирован в Dispatchers.IO, чтобы не блокировать основной поток сервера
    private suspend fun downloadImageWithAuth(url: String, token: String): BufferedImage? = withContext(Dispatchers.IO) {
        try {
            val headers = HttpHeaders().apply {
                set("Authorization", token)
                set("User-Agent", "Mozilla/5.0")
                set("Accept", "image/*")
            }
            val response = restTemplate.exchange(
                url, HttpMethod.GET, HttpEntity<Nothing>(headers), ByteArray::class.java
            )
            val imageBytes = response.body ?: return@withContext null
            ImageIO.read(ByteArrayInputStream(imageBytes))
        } catch (e: Exception) {
            log.error("❌ Ошибка загрузки изображения ($url): ${e.message}")
            null
        }
    }

    /**
     * Извлекает только красный канал из изображения.
     * Идеально подходит для черных штрих-кодов на красном фоне,
     * так как красный фон становится чисто белым, а черные линии остаются черными.
     */
    private fun extractRedChannel(image: BufferedImage): BufferedImage {
        val width = image.width
        val height = image.height
        val redImage = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)
        val redRaster = redImage.raster

        for (y in 0 until height) {
            for (x in 0 until width) {
                // Получаем пиксель в формате ARGB
                val rgb = image.getRGB(x, y)
                // Сдвигаем биты, чтобы достать только значение красного (0-255)
                val red = (rgb shr 16) and 0xFF

                // Записываем это значение как яркость серого в новое изображение
                redRaster.setSample(x, y, 0, red)
            }
        }
        return redImage
    }

    /**
     * Метод "Умной нарезки". Разрезает изображение на несколько горизонтальных полос.
     * Это позволяет обойти горизонтальные блики, царапины и засветы, передавая в сканер
     * только чистую верхнюю или нижнюю часть штрих-кода.
     */
    private fun tryDecodeSlices(
        image: BufferedImage,
        filterName: String,
        angle: Int,
        reader: MultiFormatReader
    ): DecodeDetailResult? {
        // 1. Сначала пробуем картинку целиком
        tryDecodeAllMethods(image, filterName, angle, reader)?.let { return it }

        // 2. Если не вышло - режем картинку на 3 части по горизонтали (Верх, Центр, Низ)
        val slices = 3
        val sliceHeight = image.height / slices

        if (sliceHeight < 10) return null // Защита от слишком мелких картинок

        for (i in 0 until slices) {
            val y = i * sliceHeight
            val h = if (i == slices - 1) image.height - y else sliceHeight
            val croppedSlice = image.getSubimage(0, y, image.width, h)

            tryDecodeAllMethods(
                croppedSlice,
                "$filterName (Нарезка ${i + 1}/$slices)",
                angle,
                reader
            )?.let { return it }
        }

        return null
    }

    /**
     * Увеличивает изображение (Zoom) для мелких штрих-кодов.
     */
    private fun scaleUp(image: BufferedImage, scale: Double): BufferedImage {
        val newWidth = (image.width * scale).toInt()
        val newHeight = (image.height * scale).toInt()
        val resized = BufferedImage(newWidth, newHeight, image.type)
        val g2d = resized.createGraphics()
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        g2d.drawImage(image, 0, 0, newWidth, newHeight, null)
        g2d.dispose()
        return resized
    }

    /**
     * Строгая проверка валидности штрих-кода.
     * Отсекает случайный мусор и «галлюцинации» сканера на пустых фото.
     */
    /**
     * Жесткая проверка валидности. Полностью блокирует ложные срабатывания
     * сканера на текстурах, дереве и шумах.
     */
    private fun isValidBarcodeResult(text: String, format: BarcodeFormat): Boolean {
        if (text.isBlank()) return false

        return when (format) {
            BarcodeFormat.EAN_13 -> text.length == 13 && text.all { it.isDigit() }
            BarcodeFormat.UPC_A  -> text.length == 12 && text.all { it.isDigit() }
            BarcodeFormat.EAN_8,
            BarcodeFormat.UPC_E  -> text.length == 8 && text.all { it.isDigit() }

            // CODE_128, CODE_39, CODE_93: требуем длину от 6 символов и безопасные символы
            BarcodeFormat.CODE_128,
            BarcodeFormat.CODE_39,
            BarcodeFormat.CODE_93 -> text.length >= 6 && text.all { it.isLetterOrDigit() || it == '-' || it == '_' }

            // ITF часто галлюцинирует на текстурах. Требуем, чтобы это были только цифры длиной от 6 символов
            BarcodeFormat.ITF -> text.length >= 6 && text.all { it.isDigit() }

            // QR и DataMatrix должны содержать осмысленный текст хотя бы от 3 символов
            BarcodeFormat.QR_CODE,
            BarcodeFormat.DATA_MATRIX -> text.length >= 3

            // Всё остальное, что не вошло в список, отбрасываем сразу
            else -> false
        }
    }

    fun generateQrCodeBytes(text: String, width: Int = 400, height: Int = 400): ByteArray {
        val qrWriter = QRCodeWriter()
        val bitMatrix = qrWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
        return ByteArrayOutputStream().use { outputStream ->
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream)
            outputStream.toByteArray()
        }
    }

}