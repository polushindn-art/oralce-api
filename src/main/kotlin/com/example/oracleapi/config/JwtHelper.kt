package com.example.oracleapi.config

import com.example.oracleapi.repository.userlist.UserlistRepository
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtHelper(
    private val jwtConfig: JwtConfigProperties,  // Внедряем конфиг вместо @Value
    private val userlistRepository: UserlistRepository
) {

    companion object {
        const val COOCKIENAME = "accessToken"
        val skipPaths = arrayOf(
            "/v*/monitoring/**",
            "/actuator/**",
            "/v*/phonebook/all",
            "/v*/sms/send-bot-link",
            "/login.html",
            "/idhead.html",
            "/monitor.html",
            "/glossary.html",
            "/v*/glossary/**",
            "/v*/asterisk/**",
            "/v*/max/**",
            "/v*/wakeup/**",
            "/v*/tsdlist/snByDeviceId/**",
            "/pictures/**",
            "/v1/api/health",
            "/login",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/index.html",
            "/",
            "/md-viewer.html",
            "/css/**",             // стили
            "/js/**",              // скрипты
            "/markdown/**",        // MD файлы
            "/api/markdown/**",    // API для списка файлов
            "/doc.html",
            "/webjars/**",
            "/v*/error",
            "/v*/not-found",
            "/v*/auth/**",
            "/knife4j/**",
            "/markdown/**",
            "/api/markdown/**"
        )
    }

    private val log = LoggerFactory.getLogger(JwtHelper::class.java)
    private val key: SecretKey

    init {
        require(jwtConfig.secret.isNotBlank() && jwtConfig.secret.length >= 32) {
            "JWT secret key must be at least 32 characters long. Current length: ${jwtConfig.secret.length}"
        }
        key = Keys.hmacShaKeyFor(jwtConfig.secret.toByteArray())
        log.info("JwtHelper initialized. Token expiration: ${jwtConfig.expiration}ms (${jwtConfig.expiration / 1000 / 60} minutes)")
    }

    fun createToken(subject: String): String {

        // Получаем данные пользователя из Userlist
        val userRn = userlistRepository.findRnByUsercode(subject)
        val userAgn = userlistRepository.findUserAgnByUsercode(subject)

        val now = Date()
        val expiry = Date(now.time + jwtConfig.expiration)  // Используем из конфига

        return Jwts.builder()
            .setSubject(subject)
            .claim("userRn", userRn)
            .claim("userAgn", userAgn)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    /**
     * Извлечение userRn из токена
     */
    fun extractUsername(token: String): String? {
        return parseClaims(token)?.subject
    }

    /**
     * Извлечение userAgn из токена
     */
    fun extractUserAgn(token: String): Long? {
        return try {
            val claims = parseClaims(token) ?: return null
            when (val value = claims["userAgn"]) {
                is Number -> value.toLong()
                is String -> value.toLongOrNull()
                else -> null
            }
        } catch (e: Exception) {
            log.debug("Failed to extract userAgn: ${e.message}")
            null
        }
    }

    /**
     * Извлечение произвольного claim
     */
    fun extractClaim(token: String, claimName: String): Any? {
        return parseClaims(token)?.get(claimName)
    }

    /**
     * Извлечение userRn из токена
     */
    fun extractUserRn(token: String): Long? {
        return try {
            val claims = parseClaims(token) ?: return null
            when (val value = claims["userRn"]) {
                is Number -> value.toLong()
                is String -> value.toLongOrNull()
                else -> null
            }
        } catch (e: Exception) {
            log.debug("Failed to extract userRn: ${e.message}")
            null
        }
    }

    /**
     * Проверка валидности токена
     */
    fun validateToken(token: String): Boolean {
        return parseClaims(token) != null
    }

    private fun parseClaims(token: String): Claims? {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: ExpiredJwtException) {
            log.debug("Token expired: {}", e.message)
            null
        } catch (e: MalformedJwtException) {
            log.debug("Malformed token: {}", e.message)
            null
        } catch (e: Exception) {
            log.debug("JWT validation error: {}", e.message)
            null
        }
    }
}