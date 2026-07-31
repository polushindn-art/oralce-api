package com.example.oracleapi.controller

import com.example.oracleapi.dto.asterisk.*
import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.service.ats.AsteriskService
import com.example.oracleapi.service.ats.CallAuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
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
