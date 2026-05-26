package com.example.oracleapi.common

import com.example.oracleapi.Helper
import com.example.oracleapi.exception.OracleException
import com.example.oracleapi.util.OracleErrorParser
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityManager
import org.hibernate.JDBCException
import org.slf4j.LoggerFactory
import java.sql.SQLException

abstract class BasePkg(
    protected val entityManager: EntityManager,
    protected val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(BasePkg::class.java)

    protected fun <T> execute(block: () -> T): T {
        return try {
            block()
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
            throw e
        }
    }

    fun String.toCallPrc(parameterCount: Int): String {
        val placeholders = (1..parameterCount).joinToString { "?" }
        val result = "{call ${Helper.SCHEME}.$this($placeholders)}"
        logger.error(result)
        return result
    }

    fun String.toCallFnc(parameterCount: Int): String {
        val placeholders = (1..parameterCount).joinToString { "?" }
        val result = "{?=call ${Helper.SCHEME}.$this($placeholders)}"
        logger.error(result)
        return result
    }

}