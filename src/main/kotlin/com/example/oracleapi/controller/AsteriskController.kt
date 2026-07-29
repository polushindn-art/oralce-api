package com.example.oracleapi.controller

import com.example.oracleapi.dto.asterisk.*
import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.service.ats.AsteriskService
import com.example.oracleapi.service.ats.CallAuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/v1/asterisk")
@Tag(name = "Asterisk", description = "Управление вызовами через Asterisk")
class AsteriskController(
    private val asteriskService: AsteriskService,
    private val callAuthService: CallAuthService
) : BaseController() {

    private val log = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/call")
    @Operation(
        summary = "Инициировать звонок",
        description = "Инициирует звонок с указанного номера на указанный номер через Asterisk"
    )
    fun initiateCall(
        @Valid @RequestBody request: CallDetails
    ): MyApiResponse<InitiateCallResponse> {
        val result = asteriskService.originateCall(
            internalNumber = request.from,
            externalNumber = request.to,
            callerName = request.callerId
        )

        val response = InitiateCallResponse(
            success = result,
            message = if (result) {
                "Звонок инициирован: ${request.from} → ${request.to}"
            } else {
                "Не удалось инициировать звонок: ${request.from} → ${request.to}"
            },
            data = if (result) {
                CallDetails(
                    from = request.from,
                    to = request.to,
                    callerId = request.callerId
                )
            } else {
                null
            }
        )

        return if (result) {
            success(response, response.message)
        } else {
            error(response.message, response)
        }
    }

    @PostMapping("/call-city")
    @Operation(
        summary = "Исходящий звонок с городского номера",
        description = "Совершает звонок с городского номера (DIANET_388585) на указанный номер"
    )
    fun callFromCity(
        @RequestParam("to") toNumber: String,
        @RequestParam(value = "callerId", required = false) callerId: String?
    ): MyApiResponse<CityCallResponse> {
        val result = asteriskService.originateCallFromCity(toNumber, callerId)

        val response = CityCallResponse(
            success = result,
            message = if (result) {
                "Звонок на $toNumber инициирован с городского номера"
            } else {
                "Не удалось инициировать звонок на $toNumber"
            },
            data = if (result) {
                CityCallDetails(
                    channel = "PJSIP/DIANET_388585",
                    to = toNumber,
                    callerId = callerId ?: "Городской номер"
                )
            } else {
                null
            }
        )

        return if (result) {
            success(response, response.message)
        } else {
            error(response.message, response)
        }
    }

    /**
     * Инициировать звонок для авторизации
     * POST /v1/asterisk/auth/call/initiate
     */
    @PostMapping("/auth/call/initiate")
    @Operation(
        summary = "Инициировать звонок для авторизации",
        description = "Совершает звонок на указанный номер для двухфакторной авторизации"
    )
    fun initiateAuthCall(
        @Valid @RequestBody request: AuthCallRequest
    ): MyApiResponse<AuthCallResponse> {

        val userLogin = SecurityContextHolder.getContext()
            .authentication.name

        log.info(
            "📞 Запрос на авторизацию по звонку: user={}, phone={}",
            userLogin, request.phoneNumber
        )

        try {
            if (!isValidPhoneNumber(request.phoneNumber)) {
                return error(
                    message = "Неверный формат номера телефона",
                    data = null
                )
            }

            val actionId = callAuthService.initiateAuthCall(
                phoneNumber = request.phoneNumber,
                userLogin = userLogin,
                callerId = request.callerId ?: "73852361105",
                prefix = request.prefix
            )

            log.info(
                "✅ Авторизация инициирована: actionId={}, user={}",
                actionId, userLogin
            )

            val response = AuthCallResponse(
                actionId = actionId,
                phoneNumber = request.phoneNumber,
                status = "INITIATED",
                message = "Звонок инициирован. Ожидайте ответа...",
                checkUrl = "/v1/asterisk/auth/call/status/$actionId",
                timeoutSeconds = 60
            )

            return success(response, "Звонок инициирован успешно")

        } catch (e: Exception) {
            log.error(
                "❌ Ошибка инициации звонка: user={}, error={}",
                userLogin, e.message, e
            )
            return error("Ошибка инициации звонка: ${e.message}")
        }
    }

    /**
     * Проверить статус авторизации по actionId
     * GET /v1/asterisk/auth/call/status/{actionId}
     */
    @GetMapping("/auth/call/status/{actionId}")
    @Operation(
        summary = "Проверить статус авторизации",
        description = "Проверяет статус звонка для авторизации по actionId"
    )
    fun checkAuthStatus(
        @PathVariable actionId: String
    ): MyApiResponse<AuthStatusResponse> {

        log.debug("🔍 Проверка статуса: actionId={}", actionId)

        val result = callAuthService.checkAuthStatus(actionId)

        val message = when {
            result.success -> "✅ Авторизация успешна!"
            result.status == "TIMEOUT" -> "⏰ Время ожидания истекло"
            result.status == "NOT_FOUND" -> "❌ Сессия не найдена"
            result.status == "NOANSWER" -> "❌ Абонент не ответил"
            result.status == "BUSY" -> "📞 Линия занята"
            else -> result.message
        }

        return if (result.success) {
            success(result, message)
        } else {
            error(message, result)
        }
    }

    /**
     * Получить все активные сессии (для мониторинга)
     * GET /v1/asterisk/auth/call/active
     */
    @GetMapping("/auth/call/active")
    @Operation(
        summary = "Получить активные сессии авторизации",
        description = "Возвращает список всех активных сессий авторизации"
    )
    fun getActiveSessions(): MyApiResponse<List<AuthSessionInfo>> {

        val sessions = callAuthService.getActiveSessions()

        val result = sessions.map { session ->
            AuthSessionInfo(
                actionId = session.actionId,
                phoneNumber = session.phoneNumber,
                userLogin = session.userLogin,
                status = session.status,
                createdAt = session.createdAt,
                expiresAt = session.expiresAt,
                duration = session.duration
            )
        }

        return successList(result, "Активных сессий: ${result.size}")
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        return phone.isNotBlank() && phone.matches(Regex("^\\+?[0-9]{10,15}$"))
    }

    /**
     * Голосовая авторизация (звонок с кодом)
     * POST /v1/asterisk/auth/voice/initiate
     */
    @PostMapping("/auth/voice/initiate")
    @Operation(
        summary = "Авторизация по голосовому коду",
        description = "Пройти авторизацию по голосовому коду"
    )
    fun initiateVoiceAuth(@RequestBody request: VoiceAuthRequest): MyApiResponse<VoiceAuthResponse> {

        val result = callAuthService.initiateVoiceAuth(
            phoneNumber = request.phone
        )

        return success(
            VoiceAuthResponse(
                result.authCode,
                result.phone
            )
        )

    }

}
