package com.example.oracleapi.service.sms

import com.example.oracleapi.config.SmsRuProperties
import com.example.oracleapi.dto.sms.SmsBalanceResponse
import com.example.oracleapi.dto.sms.SmsSendResponse
import com.example.oracleapi.dto.sms.SmsStatusResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Component
class SmsRuClient(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper,
    private val properties: SmsRuProperties
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    companion object {
        private const val BASE_URL = "https://sms.ru"
        private const val SUCCESS_STATUS = "OK"
        private const val SUCCESS_CODE = 100

        // Статусы сообщений
        const val STATUS_QUEUED = 100
        const val STATUS_SENT = 101
        const val STATUS_DELIVERING = 102
        const val STATUS_DELIVERED = 103
        const val STATUS_EXPIRED = 104
        const val STATUS_DELETED = 105
        const val STATUS_FAILED = 106
        const val STATUS_UNKNOWN = 107
        const val STATUS_REJECTED = 108
        const val STATUS_NO_ROUTE = 150
    }

    @PostConstruct
    fun init() {
        log.info("🔐 [SMS.RU] Конфигурация загружена: $properties")
        if (properties.apiId.isBlank()) {
            log.warn("⚠️ [SMS.RU] API ID не настроен! Укажите sms.ru.api-id в конфигурации.")
        }
    }

    /**
     * Отправить SMS
     */
    fun sendSms(
        phone: String,
        text: String,
        from: String? = null,
        test: Boolean? = null,
        ip: String? = null,
        time: Long? = null,
        ttl: Int? = null,
        daytime: Boolean = false,
        translit: Boolean = false
    ): SmsSendResponse {
        val cleanPhone = phone.replace(Regex("[^\\d,+]"), "").trim()
        val isTest = test ?: properties.testMode

        log.info("📤 [SMS.RU] Отправка SMS на номер $cleanPhone${if (isTest) " (ТЕСТОВЫЙ РЕЖИМ)" else ""}")

        val params = mutableMapOf(
            "api_id" to properties.apiId,
            "to" to cleanPhone,
            "msg" to text,
            "json" to "1"
        )

        // Добавляем опциональные параметры
        val sender = from ?: properties.defaultFrom
        if (!sender.isNullOrBlank()) {
            params["from"] = sender
        }
        if (isTest) {
            params["test"] = "1"
        }
        if (ip != null && ip.isNotBlank()) {
            params["ip"] = ip
        }
        if (time != null && time > 0) {
            params["time"] = time.toString()
        }
        if (ttl != null && ttl in 1..1440) {
            params["ttl"] = ttl.toString()
        }
        if (daytime) {
            params["daytime"] = "1"
        }
        if (translit) {
            params["translit"] = "1"
        }

        val url = UriComponentsBuilder.fromHttpUrl("$BASE_URL/sms/send")
            .queryParams(params.toMap().let {
                org.springframework.util.LinkedMultiValueMap<String, String>().apply {
                    it.forEach { (key, value) -> add(key, value) }
                }
            })
            .build()
            .toUri()

        return try {
            log.debug("📤 [SMS.RU] URL: $url")
            val response = restTemplate.getForObject(url, String::class.java)
            log.info("📤 [SMS.RU] Ответ: $response")

            parseSendResponse(response ?: "")
        } catch (e: Exception) {
            log.error("❌ [SMS.RU] Ошибка отправки SMS", e)
            SmsSendResponse(
                success = false,
                message = "Ошибка отправки: ${e.message}",
                statusCode = 500,
                statusText = e.message
            )
        }
    }

    /**
     * Отправить SMS с кодом подтверждения
     */
    fun sendVerificationCode(
        phone: String,
        code: String,
        from: String? = null,
        ip: String? = null
    ): SmsSendResponse {
        val text = """
            🔐 *Код подтверждения:*
            
            Ваш код: $code
            
            Никому не сообщайте этот код.
        """.trimIndent()
        return sendSms(phone, text, from, ip = ip)
    }

    /**
     * Отправить разные тексты на разные номера
     */
    fun sendMultiSms(
        messages: Map<String, String>,
        from: String? = null,
        test: Boolean? = null,
        ip: String? = null,
        time: Long? = null,
        ttl: Int? = null,
        daytime: Boolean = false,
        translit: Boolean = false
    ): SmsSendResponse {
        val isTest = test ?: properties.testMode

        if (messages.size > properties.maxNumbersPerRequest) {
            log.warn("⚠️ [SMS.RU] Превышено максимальное количество номеров: ${messages.size} > ${properties.maxNumbersPerRequest}")
        }

        log.info("📤 [SMS.RU] Отправка SMS на ${messages.size} номеров${if (isTest) " (ТЕСТОВЫЙ РЕЖИМ)" else ""}")

        val params = mutableMapOf(
            "api_id" to properties.apiId,
            "json" to "1"
        )

        // Добавляем номера с текстом
        messages.forEach { (phone, text) ->
            val cleanPhone = phone.replace(Regex("[^\\d]"), "").trim()
            params["to[$cleanPhone]"] = text
        }

        // Добавляем опциональные параметры
        val sender = from ?: properties.defaultFrom
        if (!sender.isNullOrBlank()) {
            params["from"] = sender
        }
        if (isTest) {
            params["test"] = "1"
        }
        if (ip != null && ip.isNotBlank()) {
            params["ip"] = ip
        }
        if (time != null && time > 0) {
            params["time"] = time.toString()
        }
        if (ttl != null && ttl in 1..1440) {
            params["ttl"] = ttl.toString()
        }
        if (daytime) {
            params["daytime"] = "1"
        }
        if (translit) {
            params["translit"] = "1"
        }

        val url = UriComponentsBuilder.fromHttpUrl("$BASE_URL/sms/send")
            .queryParams(params.toMap().let {
                org.springframework.util.LinkedMultiValueMap<String, String>().apply {
                    it.forEach { (key, value) -> add(key, value) }
                }
            })
            .build()
            .toUri()

        return try {
            log.debug("📤 [SMS.RU] URL: $url")
            val response = restTemplate.getForObject(url, String::class.java)
            log.info("📤 [SMS.RU] Ответ: $response")

            parseSendMultiResponse(response ?: "")
        } catch (e: Exception) {
            log.error("❌ [SMS.RU] Ошибка отправки SMS", e)
            SmsSendResponse(
                success = false,
                message = "Ошибка отправки: ${e.message}",
                statusCode = 500,
                statusText = e.message
            )
        }
    }

    /**
     * Проверить статус SMS
     */
    fun checkStatus(smsId: String): SmsStatusResponse? {
        val params = mapOf(
            "api_id" to properties.apiId,
            "sms_id" to smsId,
            "json" to "1"
        )

        val url = UriComponentsBuilder.fromHttpUrl("$BASE_URL/sms/status")
            .queryParams(params.toMap().let {
                org.springframework.util.LinkedMultiValueMap<String, String>().apply {
                    it.forEach { (key, value) -> add(key, value) }
                }
            })
            .build()
            .toUri()

        return try {
            val response = restTemplate.getForObject(url, String::class.java)
            log.info("📤 [SMS.RU] Статус SMS: $response")
            parseStatusResponse(response ?: "", smsId)
        } catch (e: Exception) {
            log.error("❌ [SMS.RU] Ошибка проверки статуса", e)
            null
        }
    }

    /**
     * Получить баланс
     */
    fun getBalance(): SmsBalanceResponse? {
        val params = mapOf(
            "api_id" to properties.apiId,
            "json" to "1"
        )

        val url = UriComponentsBuilder.fromHttpUrl("$BASE_URL/my/balance")
            .queryParams(params.toMap().let {
                org.springframework.util.LinkedMultiValueMap<String, String>().apply {
                    it.forEach { (key, value) -> add(key, value) }
                }
            })
            .build()
            .toUri()

        return try {
            val response = restTemplate.getForObject(url, String::class.java)
            log.info("📤 [SMS.RU] Баланс: $response")
            parseBalanceResponse(response ?: "")
        } catch (e: Exception) {
            log.error("❌ [SMS.RU] Ошибка получения баланса", e)
            null
        }
    }

    /**
     * Получить дневной лимит
     */
    fun getLimit(): SmsBalanceResponse? {
        val params = mapOf(
            "api_id" to properties.apiId,
            "json" to "1"
        )

        val url = UriComponentsBuilder.fromHttpUrl("$BASE_URL/my/limit")
            .queryParams(params.toMap().let {
                org.springframework.util.LinkedMultiValueMap<String, String>().apply {
                    it.forEach { (key, value) -> add(key, value) }
                }
            })
            .build()
            .toUri()

        return try {
            val response = restTemplate.getForObject(url, String::class.java)
            log.info("📤 [SMS.RU] Лимит: $response")
            parseLimitResponse(response ?: "")
        } catch (e: Exception) {
            log.error("❌ [SMS.RU] Ошибка получения лимита", e)
            null
        }
    }

    /**
     * Получить стоп-лист
     * Документация: https://sms.ru/api/stoplist
     */
    fun getStoplist(): List<String>? {
        val params = mapOf(
            "api_id" to properties.apiId,
            "json" to "1"
        )

        // ✅ Исправленный URL
        val url = UriComponentsBuilder.fromHttpUrl("$BASE_URL/stoplist/get")
            .queryParams(params.toMap().let {
                org.springframework.util.LinkedMultiValueMap<String, String>().apply {
                    it.forEach { (key, value) -> add(key, value) }
                }
            })
            .build()
            .toUri()

        return try {
            val response = restTemplate.getForObject(url, String::class.java)
            log.info("📤 [SMS.RU] Стоп-лист: $response")
            parseStoplistResponse(response ?: "")
        } catch (e: Exception) {
            log.error("❌ [SMS.RU] Ошибка получения стоп-листа", e)
            null
        }
    }

    // ========== HELPER METHODS ==========

    fun isApiIdConfigured(): Boolean = properties.apiId.isNotBlank()
    fun isTestMode(): Boolean = properties.testMode
    fun hasDefaultFrom(): Boolean = !properties.defaultFrom.isNullOrBlank()
    fun getMaxNumbersPerRequest(): Int = properties.maxNumbersPerRequest

    // ========== PARSE METHODS ==========

    private fun parseSendResponse(response: String): SmsSendResponse {
        return try {
            val json = objectMapper.readTree(response)

            val status = json.get("status")?.asText()
            val statusCode = json.get("status_code")?.asInt() ?: -1

            if (status == SUCCESS_STATUS && statusCode == SUCCESS_CODE) {
                val smsNode = json.get("sms")
                val balance = json.get("balance")?.asDouble()

                if (smsNode != null) {
                    val firstKey = smsNode.fieldNames().asSequence().firstOrNull()
                    val firstSms = firstKey?.let { smsNode.get(it) }

                    val smsId = firstSms?.get("sms_id")?.asText()
                    val smsStatus = firstSms?.get("status")?.asText()
                    val smsStatusCode = firstSms?.get("status_code")?.asInt()
                    val smsStatusText = firstSms?.get("status_text")?.asText()

                    val isSuccess = smsStatus == SUCCESS_STATUS && smsStatusCode == SUCCESS_CODE

                    SmsSendResponse(
                        success = isSuccess,
                        message = smsStatusText ?: if (isSuccess) "Сообщение отправлено" else "Ошибка отправки",
                        smsId = smsId,
                        balance = balance,
                        statusCode = smsStatusCode,
                        statusText = smsStatusText
                    )
                } else {
                    SmsSendResponse(
                        success = true,
                        message = "Запрос выполнен",
                        balance = balance,
                        statusCode = statusCode,
                        statusText = json.get("status_text")?.asText()
                    )
                }
            } else {
                SmsSendResponse(
                    success = false,
                    message = json.get("status_text")?.asText() ?: "Неизвестная ошибка",
                    statusCode = statusCode,
                    statusText = json.get("status_text")?.asText()
                )
            }
        } catch (e: Exception) {
            log.error("❌ [SMS.RU] Ошибка парсинга ответа: $response", e)
            SmsSendResponse(
                success = false,
                message = "Ошибка парсинга ответа: ${e.message}"
            )
        }
    }

    private fun parseSendMultiResponse(response: String): SmsSendResponse {
        return try {
            val json = objectMapper.readTree(response)

            val status = json.get("status")?.asText()
            val statusCode = json.get("status_code")?.asInt() ?: -1

            if (status == SUCCESS_STATUS && statusCode == SUCCESS_CODE) {
                val smsNode = json.get("sms")
                val balance = json.get("balance")?.asDouble()

                val results = mutableMapOf<String, Map<String, Any>>()
                var allSuccess = true
                var firstError: String? = null

                smsNode?.fields()?.asSequence()?.forEach { (phone, data) ->
                    val smsStatus = data.get("status")?.asText()
                    val smsStatusCode = data.get("status_code")?.asInt()
                    val smsStatusText = data.get("status_text")?.asText()
                    val smsId = data.get("sms_id")?.asText()

                    results[phone] = mapOf(
                        "status" to (smsStatus ?: "ERROR"),
                        "statusCode" to (smsStatusCode ?: -1),
                        "statusText" to (smsStatusText ?: "Неизвестно"),
                        "smsId" to (smsId ?: "")
                    )

                    if (smsStatus != SUCCESS_STATUS || smsStatusCode != SUCCESS_CODE) {
                        allSuccess = false
                        if (firstError == null) {
                            firstError = smsStatusText
                        }
                    }
                }

                SmsSendResponse(
                    success = allSuccess,
                    message = if (allSuccess) "Все сообщения отправлены"
                    else (firstError ?: "Некоторые сообщения не отправлены"),
                    smsId = null,
                    balance = balance,
                    statusCode = statusCode,
                    statusText = json.get("status_text")?.asText(),
                    details = results
                )
            } else {
                SmsSendResponse(
                    success = false,
                    message = json.get("status_text")?.asText() ?: "Неизвестная ошибка",
                    statusCode = statusCode,
                    statusText = json.get("status_text")?.asText()
                )
            }
        } catch (e: Exception) {
            log.error("❌ [SMS.RU] Ошибка парсинга ответа: $response", e)
            SmsSendResponse(
                success = false,
                message = "Ошибка парсинга ответа: ${e.message}"
            )
        }
    }

    private fun parseStatusResponse(response: String, smsId: String): SmsStatusResponse? {
        return try {
            val json = objectMapper.readTree(response)
            val status = json.get("status")?.asText()
            val statusCode = json.get("status_code")?.asInt() ?: -1

            if (status == SUCCESS_STATUS) {
                val smsStatus = json.get("sms")?.asText() ?: ""

                SmsStatusResponse(
                    smsId = smsId,
                    status = smsStatus,
                    statusCode = statusCode,
                    statusText = json.get("status_text")?.asText() ?: getStatusText(statusCode)
                )
            } else {
                SmsStatusResponse(
                    smsId = smsId,
                    status = "ERROR",
                    statusCode = statusCode,
                    statusText = json.get("status_text")?.asText() ?: "Неизвестная ошибка"
                )
            }
        } catch (e: Exception) {
            log.error("❌ [SMS.RU] Ошибка парсинга статуса: $response", e)
            null
        }
    }

    private fun parseBalanceResponse(response: String): SmsBalanceResponse? {
        return try {
            val json = objectMapper.readTree(response)
            val status = json.get("status")?.asText()
            val statusCode = json.get("status_code")?.asInt() ?: -1

            if (status == SUCCESS_STATUS && statusCode == SUCCESS_CODE) {
                val balance = json.get("balance")?.asDouble() ?: 0.0
                SmsBalanceResponse(
                    balance = balance,
                    limit = 0,
                    usedToday = 0
                )
            } else {
                log.warn("⚠️ [SMS.RU] Ошибка получения баланса: ${json.get("status_text")?.asText()}")
                null
            }
        } catch (e: Exception) {
            log.error("❌ [SMS.RU] Ошибка парсинга баланса: $response", e)
            null
        }
    }

    private fun parseLimitResponse(response: String): SmsBalanceResponse? {
        return try {
            val json = objectMapper.readTree(response)
            val status = json.get("status")?.asText()
            val statusCode = json.get("status_code")?.asInt() ?: -1

            if (status == SUCCESS_STATUS && statusCode == SUCCESS_CODE) {
                val totalLimit = json.get("total_limit")?.asInt() ?: 0
                val usedToday = json.get("used_today")?.asInt() ?: 0

                SmsBalanceResponse(
                    balance = 0.0,
                    limit = totalLimit,
                    usedToday = usedToday
                )
            } else {
                log.warn("⚠️ [SMS.RU] Ошибка получения лимита: ${json.get("status_text")?.asText()}")
                null
            }
        } catch (e: Exception) {
            log.error("❌ [SMS.RU] Ошибка парсинга лимита: $response", e)
            null
        }
    }

    private fun parseStoplistResponse(response: String): List<String>? {
        return try {
            val json = objectMapper.readTree(response)
            val status = json.get("status")?.asText()
            val statusCode = json.get("status_code")?.asInt() ?: -1

            if (status == SUCCESS_STATUS && statusCode == SUCCESS_CODE) {
                val stoplist = json.get("stoplist")
                val result = mutableListOf<String>()
                stoplist?.forEach { item ->
                    item.get("phone")?.asText()?.let { result.add(it) }
                }
                result
            } else {
                log.warn("⚠️ [SMS.RU] Ошибка получения стоп-листа: ${json.get("status_text")?.asText()}")
                null
            }
        } catch (e: Exception) {
            log.error("❌ [SMS.RU] Ошибка парсинга стоп-листа: $response", e)
            null
        }
    }

    /**
     * Отправить ссылку на MAIN бота
     * @param phone номер телефона
     * @param from имя отправителя
     * @param test тестовый режим
     * @param ip IP адрес пользователя
     * @param customText кастомный текст (если не указан, используется стандартный)
     */
    fun sendMainBotLink(
        phone: String,
        from: String? = null,
        test: Boolean? = null,
        ip: String? = null
    ): SmsSendResponse {
        val botLink = "https://max.ru/id2225152479_3_bot"

        val text = """
        📲 Коды подтверждения — теперь в MAX
        Подключите наш диалог в MAX и получайте коды подтверждения прямо туда 
        
        Подключиться →
        $botLink              
    """.trimIndent()

        log.info("📤 [SMS.RU] Отправка ссылки на бота на номер $phone")

        return sendSms(
            phone = phone,
            text = text,
            from = from,
            test = test,
            ip = ip
        )
    }

    private fun getStatusText(code: Int): String {
        return when (code) {
            STATUS_QUEUED -> "Запрос выполнен или сообщение находится в очереди"
            STATUS_SENT -> "Сообщение передается оператору"
            STATUS_DELIVERING -> "Сообщение отправлено (в пути)"
            STATUS_DELIVERED -> "Сообщение доставлено"
            STATUS_EXPIRED -> "Время жизни истекло"
            STATUS_DELETED -> "Удалено оператором"
            STATUS_FAILED -> "Сбой в телефоне"
            STATUS_UNKNOWN -> "Неизвестная причина"
            STATUS_REJECTED -> "Отклонено"
            STATUS_NO_ROUTE -> "Не найден маршрут на данный номер"
            else -> "Неизвестный статус"
        }
    }
}