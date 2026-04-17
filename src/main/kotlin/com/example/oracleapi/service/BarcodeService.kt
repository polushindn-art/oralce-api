package com.example.oracleapi.service

import org.springframework.stereotype.Service
import uk.org.okapibarcode.backend.DataMatrix
import uk.org.okapibarcode.backend.Symbol
import uk.org.okapibarcode.output.Java2DRenderer
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

@Service
class BarcodeService {

    fun generateGs1DataMatrix(
        inputData: String,
        width: Int = 300,
        height: Int = 300
    ): ByteArray {
        // 1. Парсим входные данные (удаляем GS и разбираем)
        val parsed = parseInputData(inputData)

        // 2. Собираем в формат для OkapiBarcode
        val okapiFormatted = buildOkapiFormat(parsed)

        println("=== GS1 PARSER DEBUG ===")
        println("Input: $inputData")
        println("Parsed: $parsed")
        println("Okapi format: $okapiFormatted")

        // 3. Генерируем штрихкод
        val dataMatrix = DataMatrix().apply {
            dataType = Symbol.DataType.GS1
            content = okapiFormatted
        }

        val matrixWidth = dataMatrix.width
        val matrixHeight = dataMatrix.height

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

    private fun parseInputData(input: String): Map<String, String> {
        val components = mutableMapOf<String, String>()

        // Удаляем все символы GS (ASCII 29) — они не нужны для парсинга
        val cleaned = input.replace("\u001D", "")

        // Парсим очищенную строку
        parseConcatenatedFormat(cleaned, components)

        return components
    }

    private fun parseConcatenatedFormat(input: String, components: MutableMap<String, String>) {
        var remaining = input

        // AI с фиксированной длиной
        val fixedLengthAIs = mapOf(
            "01" to 14   // GTIN
        )

        // AI с переменной длиной (в порядке ожидаемого появления)
        val variableAIs = listOf("21", "91", "92", "93", "94", "95", "96", "97", "98", "99")

        while (remaining.isNotEmpty()) {
            var found = false

            // Проверяем AI 01 (фиксированная длина)
            if (remaining.startsWith("01") && remaining.length >= 16) {
                val value = remaining.substring(2, 16)
                components["01"] = value
                remaining = remaining.substring(16)
                found = true
            }

            // Проверяем AI с переменной длиной
            if (!found) {
                for (ai in variableAIs) {
                    if (remaining.startsWith(ai)) {
                        val valueStart = ai.length
                        var valueEnd = remaining.length

                        // Ищем следующий AI
                        for (nextAi in fixedLengthAIs.keys + variableAIs) {
                            val index = remaining.indexOf(nextAi, valueStart)
                            if (index in 1..<valueEnd) {
                                valueEnd = index
                            }
                        }

                        val value = remaining.substring(valueStart, valueEnd)
                        components[ai] = value
                        remaining = remaining.substring(valueEnd)
                        found = true
                        break
                    }
                }
            }

            if (!found) {
                // Если ничего не нашли — возможно, это значение последнего AI
                // Добавляем остаток к последнему найденному AI
                if (components.isNotEmpty() && remaining.isNotEmpty()) {
                    val lastKey = components.keys.last()
                    components[lastKey] = components[lastKey] + remaining
                }
                break
            }
        }
    }

    private fun buildOkapiFormat(components: Map<String, String>): String {
        val result = StringBuilder()

        // Порядок AI
        val orderedAIs = listOf("01", "21", "91", "92", "93", "94", "95", "96", "97", "98", "99")

        for (ai in orderedAIs) {
            components[ai]?.let { value ->
                result.append("[$ai]$value")
            }
        }

        return result.toString()
    }
}