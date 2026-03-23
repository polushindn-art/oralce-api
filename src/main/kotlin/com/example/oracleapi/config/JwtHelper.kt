package com.example.oracleapi.config

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtHelper(
    private val jwtConfig: JwtConfigProperties  // Внедряем конфиг вместо @Value
) {

    companion object {
        const val COOCKIENAME = "accessToken"
        val skipPaths = arrayOf(
            "/api/health",
            "/auth/token",
            "/login",
            "/swagger-ui/**",      // для всех вложенных путей
            "/v3/api-docs/**",
            "/favicon.ico",
            "/doc.html",
            "/webjars/**",
            "/error",
            "/not-found",
            "/auth/**",             // если нужно открыть все под auth
            "/knife4j/**")
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
        val now = Date()
        val expiry = Date(now.time + jwtConfig.expiration)  // Используем из конфига

        return Jwts.builder()
            .setSubject(subject)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun extractUsername(token: String): String? {
        return parseClaims(token)?.subject
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