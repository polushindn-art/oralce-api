// common/BasePkgProc.kt
package com.example.oracleapi.common

import com.example.oracleapi.exception.OracleException
import com.example.oracleapi.util.OracleErrorParser
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityManager
import org.hibernate.JDBCException
import org.slf4j.LoggerFactory
import java.sql.SQLException

abstract class BasePkgProc(
    protected val entityManager: EntityManager,
    protected val objectMapper: ObjectMapper
) {
    companion object {
        const val MARK: String = "PKG_MARK"
        const val PUBLIC: String = "PKG_PUBLIC"
        const val TSDLIST: String = "PKG_TSDLIST"
        const val ORDERHEAD: String = "PKG_ORDERHEAD"
    }

    private val logger = LoggerFactory.getLogger(BasePkgProc::class.java)

    abstract val packageName: String

    protected fun <T> execute(
        procedureName: String,
        block: () -> T
    ): T {
        val startTime = System.currentTimeMillis()

        return try {
            val result = block()
            val executionTime = System.currentTimeMillis() - startTime
            result
        } catch (e: JDBCException) {
            logger.error(e.message, e)
            val sqlEx = e.sqlException ?: (e.cause as? SQLException)
            val oracleEx = if (sqlEx != null) {
                OracleErrorParser.parse(sqlEx)
            } else {
                OracleException(0, e.message ?: "Ошибка базы данных", e.message)
            }
            throw oracleEx
        } catch (e: SQLException) {
            logger.error(e.message, e)
            // Прямое SQL исключение
            val oracleEx = OracleErrorParser.parse(e)
            throw oracleEx
        } catch (e: OracleException) {
            logger.error(e.message, e)
            // Уже преобразованное исключение
            throw e
        } catch (e: Exception) {
            val executionTime = System.currentTimeMillis() - startTime
            throw e
        }
    }
}