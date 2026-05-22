package com.example.oracleapi.util

import com.example.oracleapi.exception.OracleError
import com.example.oracleapi.exception.OracleException
import org.slf4j.LoggerFactory
import java.sql.SQLException
import java.util.regex.Pattern

object OracleErrorParser {

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun parse(sqlEx: SQLException): OracleException {
        val errorCode = sqlEx.errorCode
        val fullMessage = sqlEx.message ?: "Unknown error"

        return when (errorCode) {
            20000 -> {
                parseBusinessError(fullMessage)
            }

            1403 -> {
                log.debug("ORA-01403: Data not found")
                OracleException(
                    oracleCode = 1403,
                    message = "Данные не найдены",
                    sqlState = sqlEx.sqlState,
                    details = "Запрашиваемая запись отсутствует в базе данных"
                )
            }

            else -> {
                val allErrors = extractAllErrors(fullMessage)
                val mainError = allErrors.firstOrNull()

                OracleException(
                    oracleCode = errorCode,
                    message = mainError?.message ?: "Ошибка базы данных",
                    sqlState = sqlEx.sqlState,
                    details = fullMessage.take(500),
                    nestedErrors = allErrors.takeIf { it.size > 1 }
                )
            }
        }
    }

    private fun parseBusinessError(fullMessage: String): OracleException {
        // Извлекаем первое сообщение после ORA-20000:
        val userMessagePattern = Pattern.compile("ORA-20000:\\s*(.+?)(?=\\r?\\n|ORA-)")
        val userMessageMatcher = userMessagePattern.matcher(fullMessage)

        val userMessage = if (userMessageMatcher.find()) {
            userMessageMatcher.group(1).trim()
        } else {
            "Ошибка бизнес-логики"
        }

        // Извлекаем все дополнительные ошибки (ORA-02291, ORA-01403 и т.д.)
        val additionalErrors = mutableListOf<String>()
        val oraPattern = Pattern.compile("ORA-(?!20000)\\d{5}:\\s*[^\\r\\n]+")
        val oraMatcher = oraPattern.matcher(fullMessage)
        while (oraMatcher.find()) {
            additionalErrors.add(oraMatcher.group())
        }

        val details = if (additionalErrors.isNotEmpty()) {
            additionalErrors.joinToString("; ")
        } else {
            null
        }

        log.debug("Parsed business error: message={}, details={}", userMessage, details)

        return OracleException(
            oracleCode = 20000,
            message = userMessage,
            details = details
        )
    }

    private fun extractAllErrors(fullMessage: String): List<OracleError> {
        val allErrors = mutableListOf<OracleError>()
        val pattern = Regex("ORA-(\\d{5}):\\s*([^\\r\\n]+)")
        pattern.findAll(fullMessage).forEach { match ->
            allErrors.add(
                OracleError(
                    code = match.groupValues[1].toInt(),
                    message = match.groupValues[2].trim()
                )
            )
        }
        return allErrors
    }
}