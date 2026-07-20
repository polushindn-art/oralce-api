package com.example.oracleapi.interceptor

import com.example.oracleapi.annotation.AuditTrigger
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AppContextInterceptor(
    private val jdbcTemplate: JdbcTemplate
) : HandlerInterceptor {
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        if (handler is HandlerMethod) {
            val method = handler.method
            val controller = method.declaringClass

            if (method.isAnnotationPresent(AuditTrigger::class.java) ||
                controller.isAnnotationPresent(AuditTrigger::class.java)) {
                setAuditContext(request)
            }
        }
        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        if (handler is HandlerMethod) {
            val method = handler.method
            val controller = method.declaringClass

            if (method.isAnnotationPresent(AuditTrigger::class.java) ||
                controller.isAnnotationPresent(AuditTrigger::class.java)) {
                clearAuditContext()
            }
        }
    }

    private fun setAuditContext(request: HttpServletRequest) {
        try {
            val auth = SecurityContextHolder.getContext().authentication
            val userName = auth?.name ?: "system"
            val userIp = request.remoteAddr ?: "0.0.0.0"

            jdbcTemplate.update(
                "BEGIN QREAL.PKG_APP_CONTEXT.SET_USER(?, ?); END;",
                userName,
                userIp
            )
        } catch (e: Exception) {
            // Логируем ошибку, но не прерываем запрос
        }
    }

    private fun clearAuditContext() {
        try {
            jdbcTemplate.update(
                "BEGIN QREAL.PKG_APP_CONTEXT.CLEAR; END;"
            )
        } catch (e: Exception) {
            // Логируем ошибку
        }
    }
}