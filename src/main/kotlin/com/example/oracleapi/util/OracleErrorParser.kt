package com.example.oracleapi.util

import com.example.oracleapi.exception.OracleError
import com.example.oracleapi.exception.OracleException
import org.slf4j.LoggerFactory
import java.sql.SQLException

object OracleErrorParser {

    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Получает полное сообщение из цепочки SQLException
     */
    private fun getFullMessage(sqlEx: SQLException): String {
        val messages = mutableListOf<String>()
        var currentEx: SQLException? = sqlEx

        while (currentEx != null) {
            currentEx.message?.let { messages.add(it) }
            currentEx = currentEx.nextException
        }

        val result = messages.joinToString("\n")
        log.debug("Full SQLException chain:\n$result")
        return result
    }

    fun parse(sqlEx: SQLException): OracleException {
        val errorCode = sqlEx.errorCode
        val fullMessage = getFullMessage(sqlEx)  // ← используем полное сообщение

        // Если в сообщении есть ORA-20000 - это бизнес-ошибка
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

                val businessErrors = allErrors.filter { it.code == 20000 }.takeIf { it.isNotEmpty() }
                val technicalErrors = allErrors.filter { it.code != 20000 }.takeIf { it.isNotEmpty() }

                OracleException(
                    oracleCode = errorCode,
                    message = mainError?.message ?: "Ошибка базы данных",
                    sqlState = sqlEx.sqlState,
                    details = fullMessage.take(1000),
                    businessErrors = businessErrors,
                    technicalErrors = technicalErrors,
                    nestedErrors = allErrors.takeIf { it.size > 1 }
                )
            }
        }
    }

    private fun parseBusinessError(fullMessage: String): OracleException {
        // Убираем технические ORA-коды, оставляем только ORA-20000 и текст после них
        val businessText = fullMessage
            .replace(Regex("ORA-06512:.*?(\n|$)"), "")  // убираем ORA-06512
            .replace(Regex("ORA-20000:\\s*"), "")       // убираем маркеры ORA-20000
            .replace(Regex("\\s+"), " ")                // нормализуем пробелы
            .trim()

        log.info("Business error message: $businessText")

        return OracleException.business(
            message = businessText,
            businessErrors = listOf(OracleError(code = 20000, message = businessText))
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