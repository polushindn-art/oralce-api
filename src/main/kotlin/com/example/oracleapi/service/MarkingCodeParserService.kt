package com.example.oracleapi.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MarkingCodeParserService {

    companion object {
        private const val GS = '\u001D'
    }

    data class MarkingCodeInfo(
        val isValid: Boolean = false,
        val gtin: String? = null,
        val serialNumber: String? = null,
        val verificationKey: String? = null,
        val cryptoCode: String? = null,
        val fullCis: String? = null,
        val errorMessage: String? = null
    )

    fun parseMarkingCode(code: String): MarkingCodeInfo {
        if (code.isBlank()) {
            return MarkingCodeInfo(isValid = false, errorMessage = "Пустой код")
        }

        // Разделяем по GS символу
        val parts = code.split(GS)

        // Первая часть - CIS (01 + GTIN + 21 + SN)
        val cis = parts[0]

        // Парсим CIS
        if (!cis.startsWith("01") || cis.length < 18) {
            return MarkingCodeInfo(isValid = false, errorMessage = "Неверный формат CIS")
        }

        val gtin = cis.substring(2, 16)
        if (!isValidGtin(gtin)) {
            return MarkingCodeInfo(isValid = false, errorMessage = "Неверный GTIN")
        }

        if (cis.substring(16, 18) != "21") {
            return MarkingCodeInfo(isValid = false, errorMessage = "Отсутствует AI 21")
        }

        val serialNumber = cis.substring(18)

        // Парсим остальные части
        var verificationKey: String? = null
        var cryptoCode: String? = null

        for (i in 1 until parts.size) {
            val part = parts[i]
            when {
                part.startsWith("91") -> verificationKey = part.substring(2)
                part.startsWith("92") -> cryptoCode = part.substring(2)
            }
        }

        return MarkingCodeInfo(
            isValid = true,
            gtin = gtin,
            serialNumber = serialNumber,
            verificationKey = verificationKey,
            cryptoCode = cryptoCode,
            fullCis = cis
        )
    }

    /**
     * Пересборка для OkapiBarcode - БЕЗ символов GS
     * Библиотека сама добавит необходимые разделители
     */
    fun rebuildGs1Code(parsed: MarkingCodeInfo): String {
        val result = StringBuilder()

        result.append("[01]").append(parsed.gtin)
        result.append("[21]").append(parsed.serialNumber)

        // Добавляем остальные AI без GS символов
        if (!parsed.verificationKey.isNullOrBlank()) {
            result.append("[91]").append(parsed.verificationKey)
        }

        if (!parsed.cryptoCode.isNullOrBlank()) {
            result.append("[92]").append(parsed.cryptoCode)
        }

        return result.toString()
    }

    fun isValidGtin(gtin: String): Boolean {
        if (gtin.length != 14 || !gtin.matches(Regex("\\d{14}"))) return false

        val checkDigit = gtin[13].digitToInt()
        var sum = 0

        for (i in 0 until 13) {
            val digit = gtin[i].digitToInt()
            sum += if ((i + 1) % 2 == 1) {
                digit * 3
            } else {
                digit
            }
        }

        val checksum = (10 - (sum % 10)) % 10
        return checksum == checkDigit
    }
}