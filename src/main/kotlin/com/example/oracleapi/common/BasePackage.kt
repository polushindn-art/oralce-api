package com.example.oracleapi.common

import com.example.oracleapi.Helper
import com.example.oracleapi.exception.OracleException
import com.example.oracleapi.util.OracleErrorParser
import org.hibernate.JDBCException
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.sql.CallableStatement
import java.sql.Date
import java.sql.SQLException
import java.sql.Types
import java.time.LocalDate
import javax.sql.DataSource

abstract class BasePackage(
    protected val dataSource: DataSource
) {

    companion object {
        val EMPTY = null
        const val AGNLIST = "PKG_AGNLIST"
        const val MARK_BINDING = "PKG_MARK_BINDING"
        const val MARK = "PKG_MARK"
        const val ORDERHEAD = "PKG_ORDERHEAD"
        const val ORDERSPEC = "PKG_ORDERSPEC"
        const val PRCDOCHEAD = "PKG_PRCDOCHEAD"
        const val PRCDOCSPEC = "PKG_PRCDOCSPEC"
        const val ORDERNAKLHEAD = "PKG_ORDERNAKLHEAD"
        const val ORDERNAKLSPEC = "PKG_ORDERNAKLSPEC"
        const val ORDERPAYHEAD = "PKG_ORDERPAYHEAD"
        const val ORDERPAYSPEC = "PKG_ORDERPAYSPEC"
    }

    private val logger = LoggerFactory.getLogger(BasePackage::class.java)

    abstract val pkg: String?
    abstract val method: String
    abstract val count: Int

    protected fun <T> DataSource.executeFun(
        block: (CallableStatement) -> T
    ): T {
        val executeSql = toCallFnc()
        return executeLogged {
            this.connection.use { connection ->
                return@use connection.prepareCall(executeSql).use { stmt ->
                    block(stmt)
                }
            }
        }

    }

    protected fun <T> DataSource.executePrc(
        block: (CallableStatement) -> T
    ): T {
        val executeSql = toCallPrc()
        return executeLogged {
            this.connection.use { connection ->
                return@use connection.prepareCall(executeSql).use { stmt ->
                    block(stmt)
                }
            }
        }
    }

    protected fun <T> executeLogged(block: () -> T): T {
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
            logger.error(e.message, e)
            throw OracleException(0, e.message ?: "Неизвестная ошибка", e.message)
        }
    }

    fun toCallPrc(): String {
        val schemeString = "${Helper.SCHEME}."
        val pkgString = pkg?.let { "$it." } ?: ""
        val placeholders = (1..count).joinToString { "?" }
        val result = "{call $schemeString$pkgString$method($placeholders)}"
        logger.debug(result)
        return result
    }

    fun toCallFnc(): String {
        val schemeString = "${Helper.SCHEME}."
        val pkgString = pkg?.let { "$it." } ?: ""
        val placeholders = (1..count).joinToString { "?" }
        val result = "{?=call $schemeString$pkgString$method($placeholders)}"
        logger.debug(result)
        return result
    }

    fun CallableStatement.setLongOrNull(idx: Int, value: Long?) {
        if (value != null) this.setLong(idx, value) else this.setNull(idx, Types.NUMERIC)
    }

    fun CallableStatement.setBigDecimalOrNull(idx: Int, value: BigDecimal?) {
        if (value != null) this.setBigDecimal(idx, value) else this.setNull(idx, Types.DECIMAL)
    }

    fun CallableStatement.setStringOrNull(idx: Int, value: String?) {
        if (value != null) this.setString(idx, value) else this.setNull(idx, Types.VARCHAR)
    }

    fun CallableStatement.setDateOrNull(idx: Int, value: LocalDate?) {
        if (value != null) this.setDate(idx, Date.valueOf(value)) else this.setNull(idx, Types.DATE)
    }

}