package com.example.oracleapi.config

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Instant
import org.springframework.security.core.userdetails.User

class JwtAuthenticationFilter(
    private val jwtHelper: JwtHelper,
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)
    private val pathMatcher = AntPathMatcher()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.servletPath

        if (isPublicPath(path) || request.method.equals("OPTIONS", ignoreCase = true)) {
            filterChain.doFilter(request, response)
            return
        }

        val token = extractToken(request)
        if (token == null) {
            // Оставляем WARN или DEBUG только для реальных проблем безопасности
            log.debug("В доступе отказано: не найден токен для пути {}", path)
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "Токен не найден")
            return
        }

        if (!jwtHelper.validateToken(token)) {
            log.warn("Попытка доступа с недействительным токеном для пути {}", path)
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "Неверный или просроченный токен")
            return
        }

        val username = jwtHelper.extractUsername(token)
        if (username == null) {
            log.warn("Токен валиден, но не удалось извлечь имя пользователя для пути {}", path)
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "Неверный токен")
            return
        }

        val userDetails = User.builder().username(username).password("").authorities(emptyList()).build()
        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        SecurityContextHolder.getContext().authentication = authentication

        filterChain.doFilter(request, response)
    }

    /**
     * Проверяет, является ли путь публичным (не требует аутентификации)
     */
    private fun isPublicPath(path: String): Boolean {
        return JwtHelper.skipPaths.any { pattern -> pathMatcher.match(pattern, path) }
    }

    /**
     * Извлекает токен из различных источников:
     * 1. Cookie
     * 2. Заголовок Authorization: Bearer <token>
     * 3. Параметр запроса ?token=<token> (для Swagger UI)
     */
    private fun extractToken(request: HttpServletRequest): String? {
        // 1. Проверяем cookie
        request.cookies?.firstOrNull { it.name == JwtHelper.COOCKIENAME }?.let { cookie ->
            //log.debug("Token found in cookie")
            return cookie.value
        }

        // 2. Проверяем заголовок Authorization
        val authHeader = request.getHeader("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            log.debug("Token found in Authorization header")
            return authHeader.substring(7)
        }

        // 3. Проверяем параметр запроса (для Swagger UI)
        request.getParameter("token")?.let { tokenParam ->
            log.debug("Token found in request parameter")
            return tokenParam
        }

        return null
    }

    /**
     * Отправляет JSON-ответ с ошибкой
     */
    private fun sendErrorResponse(response: HttpServletResponse, status: Int, message: String) {
        if (response.isCommitted) {
            log.warn("Response already committed, cannot send error")
            return
        }

        response.status = status
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"

        val errorBody = mapOf(
            "timestamp" to Instant.now().toString(),
            "status" to status,
            "error" to HttpStatus.valueOf(status).reasonPhrase,
            "message" to message
        )

        try {
            objectMapper.writeValue(response.writer, errorBody)
        } catch (e: Exception) {
            log.error("Failed to send error response", e)
        }
    }
}

/**
 * Класс с данными пользователя из токена
 */
data class UserDetailsFromToken(
    val username: String,
    val userRn: Long?,
    val userAgn: Long?
)