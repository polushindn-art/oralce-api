package com.example.oracleapi.service

import com.example.oracleapi.config.JwtHelper
import com.google.common.cache.CacheBuilder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.sql.DataSource
import java.sql.DriverManager
import java.sql.SQLException
import java.util.concurrent.TimeUnit

@Service
class OracleAuthService(
    private val dataSource: DataSource,
    private val jwtHelper: JwtHelper
) {
    private val log = LoggerFactory.getLogger(javaClass)

    // Только успешные проверки, TTL 30 минут
    private val authCache = CacheBuilder.newBuilder()
        .expireAfterWrite(30, TimeUnit.MINUTES)
        .maximumSize(500)
        .build<String, Boolean>()

    fun authenticate(username: String, password: String): ResultAuth {
        val key = username.lowercase()

        // Кеш только для уже подтверждённых
        if (authCache.getIfPresent(key) == true) {
            log.debug("Auth cache hit for user: $username")
            val token = jwtHelper.createToken(username)
            return ResultAuth(
                state = true,
                message = "Авторизация успешна (из кеша)",
                token = token,
                username = username,
                expiresIn = jwtHelper.getExpiration(token)
            )
        }

        // Полная проверка через Oracle
        return try {
            val jdbcUrl = dataSource.connection.metaData.url
            DriverManager.getConnection(jdbcUrl, username, password).use { conn ->
                // Успешное подключение
                authCache.put(key, true)
                ResultAuth(
                    state = true,
                    message = "Успешная аутентификация",
                    oracleMessage = "Подключение к Oracle установлено"
                )
            }
        } catch (e: SQLException) {
            // Ошибка подключения
            val oracleError = parseOracleError(e)
            log.warn("Auth failed for user $username: ${e.message}")

            ResultAuth(
                state = false,
                message = oracleError.userMessage,
                oracleMessage = oracleError.rawMessage,
                oracleCode = oracleError.errorCode
            )
        }
    }

    private fun parseOracleError(e: SQLException): OracleError {
        val message = e.message ?: "Неизвестная ошибка"
        val errorCode = e.errorCode

        val userMessage = when {
            message.contains("ORA-28000") -> "Пользователь заблокирован"
            message.contains("ORA-01017") -> "Неверный логин или пароль"
            message.contains("ORA-28001") -> "Пароль истёк"
            message.contains("ORA-28007") -> "Пароль нельзя использовать повторно"
            message.contains("ORA-28003") -> "Пароль не соответствует политике"
            message.contains("ORA-28011") -> "Срок действия пароля истекает"
            message.contains("ORA-12154") -> "Не удалось разрешить строку подключения"
            message.contains("ORA-12541") -> "Нет слушателя (TNS)"
            message.contains("ORA-12514") -> "Слушатель не знает о службе"
            else -> "Ошибка аутентификации: $message"
        }

        return OracleError(
            errorCode = errorCode,
            rawMessage = message,
            userMessage = userMessage
        )
    }

    // Принудительный сброс кеша
    fun invalidateUser(username: String) {
        val key = username.lowercase()
        authCache.invalidate(key)
        log.info("Cache invalidated for user: $username")
    }

    data class ResultAuth(
        val state: Boolean,
        val token: String? = null,
        val username: String? = null,
        val expiresIn: Long? = null,
        val message: String,
        val oracleMessage: String? = null,
        val oracleCode: Int? = null
    )

    data class OracleError(
        val errorCode: Int,
        val rawMessage: String,
        val userMessage: String
    )
}