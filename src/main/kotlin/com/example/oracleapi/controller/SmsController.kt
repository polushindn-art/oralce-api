package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.sms.SmsBalanceResponse
import com.example.oracleapi.dto.sms.SmsMultiSendRequest
import com.example.oracleapi.dto.sms.SmsSendMainBotLinkRequest
import com.example.oracleapi.dto.sms.SmsSendRequest
import com.example.oracleapi.dto.sms.SmsSendResponse
import com.example.oracleapi.dto.sms.SmsStatusResponse
import com.example.oracleapi.dto.sms.SmsVerificationResponse
import com.example.oracleapi.service.sms.SmsRuService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import kotlin.math.pow

@RestController
@RequestMapping("/v1/sms")
@Tag(name = "SMS", description = "Сервис отправки SMS через SMS.RU")
class SmsController(
    private val smsRuService: SmsRuService
) : BaseController() {

    private val log = LoggerFactory.getLogger(this::class.java)

    // ============================================================
    // ОТПРАВКА SMS
    // ============================================================

    @PostMapping("/send")
    @Operation(
        summary = "Отправить SMS",
        description = "Отправляет SMS через сервис SMS.RU"
    )
    fun sendSms(
        @Valid @RequestBody request: SmsSendRequest
    ): MyApiResponse<SmsSendResponse> {
        log.info("📤 [SMS] Отправка SMS на номер ${request.phone}")

        val result = smsRuService.sendSms(
            phone = request.phone,
            text = request.text,
            from = request.from,
            test = request.test,
            ip = request.ip,
            time = request.time,
            ttl = request.ttl,
            daytime = request.daytime ?: false,
            translit = request.translit ?: false
        )

        return if (result.success) {
            success(result, "SMS отправлена")
        } else {
            error(result.message, result)
        }
    }

    @PostMapping("/send-multi")
    @Operation(
        summary = "Отправить разные SMS на разные номера",
        description = "Отправляет разные тексты на разные номера через SMS.RU"
    )
    fun sendMultiSms(
        @Valid @RequestBody request: SmsMultiSendRequest
    ): MyApiResponse<SmsSendResponse> {
        log.info("📤 [SMS] Отправка SMS на ${request.messages.size} номеров")

        val result = smsRuService.sendMultiSms(
            messages = request.messages,
            from = request.from,
            test = request.test,
            ip = request.ip,
            time = request.time,
            ttl = request.ttl,
            daytime = request.daytime ?: false,
            translit = request.translit ?: false
        )

        return if (result.success) {
            success(result, "SMS отправлены")
        } else {
            error(result.message, result)
        }
    }

    // ============================================================
    // КОД ПОДТВЕРЖДЕНИЯ
    // ============================================================

    @PostMapping("/verification")
    @Operation(
        summary = "Отправить код подтверждения",
        description = "Генерирует и отправляет код подтверждения на номер через SMS.RU"
    )
    fun sendVerificationCode(
        @RequestParam phone: String,
        @RequestParam(defaultValue = "4") length: Int,
        @RequestParam(required = false) from: String?,
        @RequestParam(required = false) ip: String?
    ): MyApiResponse<SmsVerificationResponse> {
        log.info("🔐 [SMS] Отправка кода подтверждения на номер $phone")

        val code = if (length == 4) {
            (1000..9999).random().toString()
        } else {
            (100000..999999).random().toString()
        }

        val result = smsRuService.sendVerificationCode(phone, code, from, ip)

        return if (result.success) {
            success(
                SmsVerificationResponse(
                    phone = phone,
                    code = code,
                    smsId = result.smsId,
                    balance = result.balance
                ),
                "Код подтверждения отправлен"
            )
        } else {
            error(
                result.message,
                SmsVerificationResponse(
                    phone = phone,
                    code = code
                )
            )
        }
    }

    // ============================================================
    // СТАТУСЫ И ИНФОРМАЦИЯ
    // ============================================================

    @GetMapping("/status")
    @Operation(
        summary = "Проверить статус SMS",
        description = "Проверяет статус отправленного SMS по ID"
    )
    fun checkSmsStatus(
        @RequestParam smsId: String
    ): MyApiResponse<SmsStatusResponse?> {
        log.info("📤 [SMS] Проверка статуса SMS: $smsId")

        val result = smsRuService.checkStatus(smsId)

        return if (result != null) {
            success(result, "Статус получен")
        } else {
            error("Не удалось получить статус")
        }
    }

    @GetMapping("/balance")
    @Operation(
        summary = "Получить баланс",
        description = "Возвращает текущий баланс аккаунта SMS.RU"
    )
    fun getSmsBalance(): MyApiResponse<SmsBalanceResponse?> {
        log.info("📤 [SMS] Запрос баланса")

        val result = smsRuService.getBalance()

        return if (result != null) {
            success(result, "Баланс получен")
        } else {
            error("Не удалось получить баланс")
        }
    }

    @GetMapping("/limit")
    @Operation(
        summary = "Получить дневной лимит",
        description = "Возвращает дневной лимит и количество использованных SMS"
    )
    fun getSmsLimit(): MyApiResponse<SmsBalanceResponse?> {
        log.info("📤 [SMS] Запрос дневного лимита")

        val result = smsRuService.getLimit()

        return if (result != null) {
            success(result, "Лимит получен")
        } else {
            error("Не удалось получить лимит")
        }
    }

    @GetMapping("/stoplist")
    @Operation(
        summary = "Получить стоп-лист",
        description = "Возвращает список номеров в стоп-листе SMS.RU"
    )
    fun getStoplist(): MyApiResponse<List<String>?> {
        log.info("📤 [SMS] Запрос стоп-листа")

        val result = smsRuService.getStoplist()

        return if (result != null) {
            success(result, "Стоп-лист получен")
        } else {
            error("Не удалось получить стоп-лист")
        }
    }

    @GetMapping("/config")
    @Operation(
        summary = "Проверить конфигурацию SMS.RU",
        description = "Возвращает статус конфигурации SMS.RU"
    )
    fun checkSmsConfig(): MyApiResponse<Map<String, Any>> {
        return success(
            mapOf(
                "apiIdConfigured" to smsRuService.isApiIdConfigured(),
                "testMode" to smsRuService.isTestMode(),
                "hasDefaultFrom" to smsRuService.hasDefaultFrom(),
                "maxNumbersPerRequest" to smsRuService.getMaxNumbersPerRequest()
            ),
            "Конфигурация проверена"
        )
    }

    // ============================================================
// ОТПРАВКА ССЫЛКИ НА БОТА
// ============================================================

    @PostMapping("/send-bot-link")
    @Operation(
        summary = "Отправить ссылку на MAIN бота",
        description = "Отправляет SMS со ссылкой на бота https://max.ru/id2225152479_3_bot"
    )
    fun sendMainBotLink(
        @Valid @RequestBody request: SmsSendMainBotLinkRequest
    ): MyApiResponse<SmsSendResponse> {
        log.info("📤 [SMS] Отправка ссылки на бота на номер ${request.phone}")

        val result = smsRuService.sendMainBotLink(
            phone = request.phone,
            from = request.from,
            test = request.test,
            ip = request.ip
        )

        return if (result.success) {
            success(result, "Ссылка на бота отправлена")
        } else {
            error(result.message, result)
        }
    }

}