package com.example.oracleapi.config

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.MDC
import org.springframework.stereotype.Component

/**
 * Фильтр для логирования IP-адреса клиента в контексте SLF4J MDC.
 * Учитывает прокси-заголовки для корректного определения реального адреса.
 */
@Component
class DeviceLoggingFilter : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        try {
            val clientIp = httpRequest.getHeader("X-Forwarded-For")?.split(",")?.first()?.trim()
                ?: httpRequest.remoteAddr
                ?: "UNKNOWN_IP"

            MDC.put("clientIp", clientIp)

            chain.doFilter(request, response)
        } finally {
            MDC.clear()
        }
    }
}