package com.example.oracleapi.config

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

/**
 * Фильтр для ограничения частоты запросов (Rate Limiting) по IP-адресу клиента.
 * Защищает API от лавинообразных повторов с ТСД и интеграционных систем.
 */
@Component
class RateLimitFilter(
    @param:Value("\${app.ratelimit.capacity:150}") private val capacity: Long
) : Filter {

    private val buckets = ConcurrentHashMap<String, Bucket>()

    private fun resolveBucket(key: String): Bucket {
        return buckets.computeIfAbsent(key) {
            // Емкость 150 запросов в секунду с учетом того, что под одним IP может быть весь склад (NAT)
            Bucket.builder()
                .addLimit(Bandwidth.builder().capacity(capacity).refillGreedy(capacity, Duration.ofSeconds(1)).build())
                .build()
        }
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val path = httpRequest.requestURI

        // 1. Пропускаем системные эндпоинты и дашборд логов без ограничений
        if (path.startsWith("/actuator") || path.startsWith("/v1/logs")) {
            chain.doFilter(request, response)
            return
        }

        // 2. Получаем реальный IP-адрес клиента с учетом прокси
        val clientIp = httpRequest.getHeader("X-Forwarded-For")?.split(",")?.first()?.trim()
            ?: httpRequest.remoteAddr
            ?: "UNKNOWN_IP"

        // 3. Белый список для доверенных хостов (например, сервера 1С или локальной сети)
        /*if (clientIp == "127.0.0.1" || clientIp.startsWith("192.168.100.")) {
            chain.doFilter(request, response)
            return
        }*/

        val bucket = resolveBucket(clientIp)
        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response)
        } else {
            val httpResponse = response as HttpServletResponse
            httpResponse.status = HttpStatus.TOO_MANY_REQUESTS.value()
            httpResponse.writer.write("Rate limit exceeded")
        }
    }
}