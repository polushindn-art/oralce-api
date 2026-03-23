package com.example.oracleapi.config

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {

    private val log = LoggerFactory.getLogger(CustomAuthenticationEntryPoint::class.java)
    private val objectMapper = ObjectMapper()

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val path = request.servletPath
        log.warn("Authentication failed for path: {}, reason: {}", path, authException.message)

        // Проверяем, ожидает ли клиент JSON
        val acceptHeader = request.getHeader(HttpHeaders.ACCEPT)
        val expectsJson = acceptHeader != null && acceptHeader.contains("application/json")

        if (expectsJson) {
            sendJsonError(response, authException, path)
        } else {
            // Для браузера можно перенаправить на страницу входа
            // response.sendRedirect("/login")

            // Или тоже вернуть JSON (универсальный вариант)
            sendJsonError(response, authException, path)
        }
    }

    private fun sendJsonError(
        response: HttpServletResponse,
        authException: AuthenticationException,
        path: String
    ) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"

        val errorBody = mapOf(
            "timestamp" to Instant.now().toString(),
            "status" to HttpServletResponse.SC_UNAUTHORIZED,
            "error" to "Unauthorized",
            "message" to "Доступ запрещён. Требуется аутентификация.",
            "details" to (authException.message ?: "No details"),
            "path" to path
        )

        try {
            objectMapper.writeValue(response.writer, errorBody)
        } catch (e: Exception) {
            log.error("Failed to send error response", e)
            // Если не удалось отправить JSON, отправляем простой текст
            response.writer.write("Unauthorized")
        }
    }
}