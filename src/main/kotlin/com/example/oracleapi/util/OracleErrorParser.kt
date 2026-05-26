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

        // ✅ Если в сообщении есть ORA-20000 - это бизнес-ошибка
        if (fullMessage.contains("ORA-20000")) {
            return parseBusinessError(fullMessage)
        }

        return when (errorCode) {
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

                // Разделяем на бизнес-ошибки (если вдруг) и технические
                val businessErrors = allErrors.filter { it.code == 20000 }.takeIf { it.isNotEmpty() }
                val technicalErrors = allErrors.filter { it.code != 20000 }.takeIf { it.isNotEmpty() }

                OracleException(
                    oracleCode = errorCode,
                    message = mainError?.message ?: "Ошибка базы данных",
                    sqlState = sqlEx.sqlState,
                    details = fullMessage.take(500),
                    businessErrors = businessErrors,
                    technicalErrors = technicalErrors,
                    nestedErrors = allErrors.takeIf { it.size > 1 }
                )
            }
        }
    }

    private fun parseBusinessError(fullMessage: String): OracleException {
        val businessErrors = mutableListOf<OracleError>()
        val technicalErrors = mutableListOf<OracleError>()

        val pattern = Pattern.compile("ORA-(\\d{5}):\\s*(.+?)(?=\\r?\\n|ORA-|$)")
        val matcher = pattern.matcher(fullMessage)

        while (matcher.find()) {
            val code = matcher.group(1).toInt()
            val msg = matcher.group(2).trim().replace(Regex("\\s+"), " ")

            if (code == 20000) {
                businessErrors.add(OracleError(code = code, message = msg))
            } else {
                technicalErrors.add(OracleError(code = code, message = msg))
            }
        }

        // Собираем ВСЕ бизнес-ошибки в одно сообщение
        val userMessage = if (businessErrors.isNotEmpty()) {
            businessErrors.joinToString("; ") { it.message }
        } else {
            "Ошибка бизнес-логики"
        }

        val allErrors = (businessErrors + technicalErrors).takeIf { it.isNotEmpty() }

        return OracleException.business(
            message = userMessage,
            businessErrors = businessErrors.takeIf { it.isNotEmpty() },
            technicalErrors = technicalErrors.takeIf { it.isNotEmpty() },
            nestedErrors = allErrors
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