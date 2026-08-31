package com.example.oracleapi.service.max.verification

import com.example.oracleapi.dto.max.AgeVerificationResponse
import com.example.oracleapi.dto.max.DisabledPersonStatus
import com.example.oracleapi.dto.max.LargeFamilyStatus
import com.example.oracleapi.dto.max.StudentStatus
import com.example.oracleapi.dto.max.common.MaxApiResponse
import com.example.oracleapi.dto.max.common.MaxErrorDetails
import com.example.oracleapi.service.max.common.MaxApiClientService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MaxVerificationService(
    private val apiClient: MaxApiClientService
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun verifyAge(sessionId: String): AgeVerificationResponse {
        // 1. Валидация
        if (!sessionId.startsWith("https://www.gosuslugi.ru/m")) {
            return AgeVerificationResponse(
                isAdult = false,
                error = "Неверный формат QR-кода. Пожалуйста, покажите актуальный QR-код из приложения MAX"
            )
        }

        // 2. Вызов API
        val response = try {
            apiClient.verifyAge(sessionId, withDetails = true)
        } catch (e: Exception) {
            log.error("Ошибка вызова MAX API", e)
            val maxError = extractMaxError(e)
            return if (maxError != null) {
                handleErrorResponse(maxError)
            } else {
                AgeVerificationResponse(
                    isAdult = false,
                    error = "Сервис временно недоступен. Попробуйте позже или обратитесь к администратору"
                )
            }
        }

        // 3. Обработка ответа
        return when (response.status) {
            "success" -> handleSuccess(response)
            "error" -> handleErrorResponse(response.error)
            else -> AgeVerificationResponse(
                isAdult = false,
                error = "Сервис временно недоступен. Попробуйте позже"
            )
        }
    }

    private fun extractMaxError(e: Exception): MaxErrorDetails? {
        val message = e.message ?: return null
        val codePattern = "\"code\":\"([A-Z_]+)\"".toRegex()
        val matchResult = codePattern.find(message)
        val code = matchResult?.groupValues?.get(1) ?: return null

        val messagePattern = "\"message\":\"([^\"]+)\"".toRegex()
        val messageMatch = messagePattern.find(message)
        val errorMessage = messageMatch?.groupValues?.get(1) ?: "Unknown error"

        val detailsPattern = "\"details\":\"([^\"]+)\"".toRegex()
        val detailsMatch = detailsPattern.find(message)
        val details = detailsMatch?.groupValues?.get(1)

        return MaxErrorDetails(
            code = code,
            message = errorMessage,
            details = details
        )
    }

    private fun handleSuccess(response: MaxApiResponse): AgeVerificationResponse {
        val data = response.data
            ?: return AgeVerificationResponse(
                isAdult = false,
                error = "Ошибка получения данных. Попробуйте еще раз"
            )

        val details = data.verificationDetails

        val isAdult = details.adult.status
        log.info("✅ Возраст подтвержден: $isAdult")

        val studentStatus = details.student?.let { studentData ->
            StudentStatus(
                status = studentData.status,
                educationForm = studentData.studentticketsEducationForm,
                organizationName = studentData.studentticketsOrganizationName
            )
        }

        val familyStatus = details.certificateLargeFamily?.let { familyData ->
            LargeFamilyStatus(
                status = familyData.status,
                district = familyData.district
            )
        }

        val pensionerStatus = details.pensioner?.status

        val disabledStatus = details.disabledPerson?.let { disabledData ->
            DisabledPersonStatus(
                status = disabledData.status,
                disabilityGroup = disabledData.codeGroupDisability
            )
        }

        return AgeVerificationResponse(
            isAdult = isAdult,
            studentStatus = studentStatus,
            largeFamilyStatus = familyStatus,
            pensionerStatus = pensionerStatus,
            disabledPersonStatus = disabledStatus,
            error = null
        )
    }

    private fun handleErrorResponse(error: MaxErrorDetails?): AgeVerificationResponse {
        if (error == null) {
            return AgeVerificationResponse(
                isAdult = false,
                error = "Неизвестная ошибка. Попробуйте еще раз"
            )
        }

        log.warn("MAX API error: code={}, message={}, details={}", error.code, error.message, error.details)

        val userMessage = when (error.code) {
            "SESSION_NOT_FOUND" -> "QR-код недействителен. Попросите покупателя обновить QR-код в приложении MAX"
            "SESSION_EXPIRED" -> "QR-код устарел. Попросите покупателя обновить QR-код (обновляется каждые 30 секунд)"
            "UNAUTHORIZED" -> "Ошибка авторизации. Обратитесь к администратору"
            "RATE_LIMIT_EXCEEDED" -> "Слишком много запросов. Подождите несколько секунд и попробуйте снова"
            "INVALID_SESSION" -> "Неверный QR-код. Попросите покупателя показать QR-код из приложения MAX"
            else -> "Ошибка проверки. Попросите покупателя показать QR-код еще раз"
        }

        return AgeVerificationResponse(
            isAdult = false,
            error = userMessage
        )
    }
}