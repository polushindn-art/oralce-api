package com.example.oracleapi.service.max.common

import com.example.oracleapi.config.MaxApiProperties
import com.example.oracleapi.dto.max.common.MaxApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.web.util.UriComponentsBuilder

@Service
class MaxApiClient(
    private val restTemplate: RestTemplate,
    private val properties: MaxApiProperties
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun verifyAge(sessionId: String, withDetails: Boolean = true): MaxApiResponse {
        val uri = UriComponentsBuilder.fromHttpUrl(properties.ageVerificationUrl)
            .queryParam("session_id", sessionId)
            .queryParam("verification_details", withDetails)
            .build()
            .toUri()

        log.info("📤 Запрос к MAX API: $uri")

        val headers = HttpHeaders().apply {
            set("Authorization", properties.verificationToken)
            set("Content-Type", "application/json")
        }

        return try {
            // ✅ Теперь сразу парсим в MaxApiResponse
            val response = restTemplate.exchange<MaxApiResponse>(
                uri,
                HttpMethod.GET,
                HttpEntity<Nothing>(headers)
            )
            response.body ?: throw RestClientException("Empty response from MAX API")
        } catch (e: RestClientException) {
            log.error("❌ Ошибка при вызове MAX API", e)
            throw RestClientException("Failed to call MAX verification API: ${e.message}", e)
        }
    }
}