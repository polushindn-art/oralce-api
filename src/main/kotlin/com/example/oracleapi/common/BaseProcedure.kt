package com.example.oracleapi.common

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityManager
import org.slf4j.LoggerFactory
import java.sql.Clob
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.fasterxml.jackson.module.kotlin.readValue

/**
 * Базовый класс для всех процедур Oracle
 */
abstract class BaseProcedure(
    protected val entityManager: EntityManager,
    protected val objectMapper: ObjectMapper
) {
    protected val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Название пакета Oracle
     */
    companion object {
        const val MARK: String = "PKG_MARK"
        const val PUBLIC: String = "PKG_PUBLIC"
        const val TSDLIST: String = "PKG_TSDLIST"
    }
    abstract val packageName: String

    /**
     * Логирование
     */
    protected fun <T> execute(
        procedureName: String,
        block: () -> T
    ): T {
        val fullProcedureName = "$packageName.$procedureName"
        val startTime = System.currentTimeMillis()
        val threadName = Thread.currentThread().name

        log.info("[{}] Начало выполнения, поток: {}",
            fullProcedureName, threadName)

        return try {
            val result = block()

            val executionTime = System.currentTimeMillis() - startTime
            log.info("[{}] Успешно выполнено за {} мс, поток: {}",
                fullProcedureName, executionTime, threadName)

            result
        } catch (e: Exception) {
            val executionTime = System.currentTimeMillis() - startTime
            log.error("[{}] Ошибка выполнения за {} мс: {}",
                fullProcedureName, executionTime, e.message, e)
            throw e
        }
    }

    /**
     * Универсальный метод для вызова Oracle функции, возвращающей JSON
     * @param functionName имя функции в пакете
     * @param params параметры функции (могут быть null)
     * @return результат, преобразованный в указанный тип T
     */
    protected inline fun <reified T> callFunction(
        functionName: String,
        vararg params: Any?
    ): T {
        val fullName = "$packageName.$functionName"
        val startTime = System.currentTimeMillis()

        try {
            // Формируем SQL запрос
            val placeholders = params.joinToString(",") { "?" }
            val sql = "SELECT $fullName($placeholders) FROM DUAL"

            log.debug("[{}] SQL: {}", fullName, sql)

            // Создаем и выполняем запрос
            val query = entityManager.createNativeQuery(sql)
            params.forEachIndexed { index, param ->
                when (param) {
                    null -> query.setParameter(index + 1, null)
                    else -> query.setParameter(index + 1, param)
                }
            }

            // Получаем результат
            val jsonString = when (val result = query.singleResult) {
                is String -> result
                is Clob -> result.getSubString(1, result.length().toInt())
                else -> result?.toString() ?: "null"
            }

            val executionTime = System.currentTimeMillis() - startTime
            log.debug("[{}] Выполнено за {} мс, длина JSON: {}",
                fullName, executionTime, jsonString.length)

            // Парсим JSON в указанный тип
            return objectMapper.readValue(jsonString)

        } catch (e: Exception) {
            val executionTime = System.currentTimeMillis() - startTime
            log.error("[{}] Ошибка за {} мс: {}", fullName, executionTime, e.message, e)
            throw RuntimeException("Ошибка вызова функции $fullName: ${e.message}", e)
        }
    }

    /**
     * Удобный метод для вызова функции, возвращающей список
     */
    protected inline fun <reified T> callListFunction(
        functionName: String,
        vararg params: Any?
    ): List<T> {
        return callFunction<List<T>>(functionName, *params)
    }

    /**
     * Метод для вызова функции, возвращающей простое значение (не JSON)
     */
    protected fun callScalarFunction(
        functionName: String,
        vararg params: Any?
    ): Any? {
        val fullName = "$packageName.$functionName"

        try {
            val placeholders = params.joinToString(",") { "?" }
            val sql = "SELECT $fullName($placeholders) FROM DUAL"

            val query = entityManager.createNativeQuery(sql)
            params.forEachIndexed { index, param ->
                when (param) {
                    null -> query.setParameter(index + 1, null)
                    else -> query.setParameter(index + 1, param)
                }
            }

            return query.singleResult

        } catch (e: Exception) {
            log.error("[{}] Ошибка вызова: {}", fullName, e.message, e)
            throw e
        }
    }

    /**
     * Текущее время для логов
     */
    protected fun currentTimestamp(): String =
        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

}