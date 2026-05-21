// common/BasePkgProc.kt (улучшенная версия)
package com.example.oracleapi.common

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityManager
import org.slf4j.LoggerFactory
import java.sql.CallableStatement
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

abstract class BasePkgProc(
    protected val entityManager: EntityManager,
    protected val objectMapper: ObjectMapper
) {
    protected val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        const val MARK: String = "PKG_MARK"
        const val PUBLIC: String = "PKG_PUBLIC"
        const val TSDLIST: String = "PKG_TSDLIST"
        const val ORDERHEAD: String = "PKG_ORDERHEAD"
    }

    abstract val packageName: String

    protected fun currentTimestamp(): String =
        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)

    protected fun <T> execute(
        procedureName: String,
        block: () -> T
    ): T {
        val fullProcedureName = "$packageName.$procedureName"
        val startTime = System.currentTimeMillis()
        val threadName = Thread.currentThread().name

        log.info("[{}] Начало выполнения, поток: {}", fullProcedureName, threadName)

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
     * Вызов процедуры с возможностью получения OUT параметров
     * @param procedureName имя процедуры
     * @param paramConfigurator лямбда для настройки параметров, возвращает массив OUT значений
     */
    protected fun callProcedureWithOut(
        procedureName: String,
        paramConfigurator: (CallableStatement) -> Array<Any?>
    ): Array<Any?> {
        val fullName = "$packageName.$procedureName"

        return execute(procedureName) {
            // Определяем количество параметров (нужно знать заранее)
            // Для ORDERHEAD.INS это 27 параметров
            val paramCount = getParamCount(procedureName)
            val placeholders = (1..paramCount).joinToString(",") { "?" }
            val sql = "{call $fullName($placeholders)}"

            var result: Array<Any?>? = null

            entityManager.unwrap(jakarta.persistence.EntityManager::class.java)
                .unwrap(org.hibernate.Session::class.java)
                .doWork { connection ->
                    connection.prepareCall(sql).use { callableStatement ->
                        result = paramConfigurator(callableStatement)
                        callableStatement.execute()
                    }
                }

            result ?: emptyArray()
        }
    }

    /**
     * Количество параметров процедуры (должно быть переопределено в наследнике)
     */
    protected open fun getParamCount(procedureName: String): Int {
        // По умолчанию возвращаем 0, нужно переопределить
        log.warn("getParamCount не переопределен для процедуры {}.{}", packageName, procedureName)
        return 0
    }
}