package com.example.oracleapi.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.org.okapibarcode.backend.DataMatrix
import uk.org.okapibarcode.backend.Symbol
import uk.org.okapibarcode.output.Java2DRenderer
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

@Service
class GS1DataMatrixService(
    private val parserService: MarkingCodeParserService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun generateAutoDataMatrix(
        inputData: String,
        width: Int = 300,
        height: Int = 300
    ): ByteArray {
        // Парсим код
        val parsed = parserService.parseMarkingCode(inputData)

        if (!parsed.isValid) {
            log.warn("Invalid code: ${parsed.errorMessage}")
            return generatePlainDataMatrix(inputData, width, height)
        }

        // Пересобираем в правильный GS1 формат
        val gs1Formatted = parserService.rebuildGs1Code(parsed)

        log.info("GTIN: {}, SN: {}", parsed.gtin, parsed.serialNumber?.take(20))
        log.info("GS1 formatted: {}", gs1Formatted.take(80))

        return generateGs1DataMatrix(gs1Formatted, width, height)
    }

    fun generateGs1DataMatrix(
        inputData: String,
        width: Int = 300,
        height: Int = 300
    ): ByteArray {
        val dataMatrix = DataMatrix().apply {
            dataType = Symbol.DataType.GS1
            content = inputData
        }
        return renderToImage(dataMatrix, width, height)
    }

    fun generatePlainDataMatrix(
        data: String,
        width: Int = 300,
        height: Int = 300
    ): ByteArray {
        val dataMatrix = DataMatrix().apply {
            content = data
        }
        return renderToImage(dataMatrix, width, height)
    }

    private fun renderToImage(
        dataMatrix: DataMatrix,
        width: Int,
        height: Int
    ): ByteArray {
        val matrixWidth = dataMatrix.width
        val matrixHeight = dataMatrix.height

        if (matrixWidth == 0 || matrixHeight == 0) {
            throw IllegalStateException("Invalid DataMatrix size")
        }

        val scaleX = width.toDouble() / matrixWidth.toDouble()
        val scaleY = height.toDouble() / matrixHeight.toDouble()
        val scale = minOf(scaleX, scaleY).coerceAtLeast(1.0)

        val image = BufferedImage(
            (matrixWidth * scale).toInt(),
            (matrixHeight * scale).toInt(),
            BufferedImage.TYPE_INT_RGB
        )
        val graphics = image.createGraphics()

        graphics.color = Color.WHITE
        graphics.fillRect(0, 0, image.width, image.height)

        val renderer = Java2DRenderer(graphics, scale, Color.WHITE, Color.BLACK)
        renderer.render(dataMatrix)
        graphics.dispose()

        ByteArrayOutputStream().use { baos ->
            ImageIO.write(image, "png", baos)
            return baos.toByteArray()
        }
    }
}