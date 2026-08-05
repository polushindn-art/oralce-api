package com.example.oracleapi.service.max.authAgn

import com.example.oracleapi.config.MaxApiProperties
import com.example.oracleapi.dto.agnphonenumberlist.AgnphonenumberlistDto
import com.example.oracleapi.service.agnphonenumber.AgnPhoneService
import ezvcard.Ezvcard
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class MaxAuthPollingService(
    private val restTemplate: RestTemplate,
    private val properties: MaxApiProperties,
    private val botAuthClient: MaxBotAuthClient,
    private val agnPhoneService: AgnPhoneService
) {

    private val log = LoggerFactory.getLogger(this::class.java)
    private var marker: Long? = null

    @Scheduled(fixedDelay = 2000)
    fun pollUpdates() {
        try {
            val uriBuilder = UriComponentsBuilder.fromHttpUrl("${properties.botApiUrl}/updates")
                .queryParam("limit", 10)
                .queryParam("timeout", 30)

            if (marker != null) {
                uriBuilder.queryParam("marker", marker)
            }

            val url = uriBuilder.build().toUri()

            val headers = HttpHeaders().apply {
                set("Authorization", properties.botAuthToken)
            }

            val response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                HttpEntity<Nothing>(headers),
                Map::class.java
            )

            val body = response.body
            val updates = body?.get("updates") as? List<*> ?: return
            val newMarker = (body["marker"] as? Number)?.toLong()

            if (newMarker != null && newMarker != marker) {
                marker = newMarker
                log.info("🤖 [Auth Bot] Получены новые обновления. Маркер: $marker")
            }

            updates.forEach { updateRaw ->
                val update = updateRaw as? Map<*, *> ?: return@forEach

                when (val updateType = update["update_type"] as? String) {
                    "bot_started" -> {
                        val userId = (update["user_id"] as? Number)?.toString() ?: return@forEach
                        val chatId = (update["chat_id"] as? Number)?.toString() ?: return@forEach
                        log.info("🚀 [Auth Bot] Бот запущен: userId=$userId, chatId=$chatId")
                        sendMainMenu(userId, chatId)
                    }

                    "message_created" -> {
                        val message = update["message"] as? Map<*, *> ?: return@forEach
                        val sender = message["sender"] as? Map<*, *>
                        val recipient = message["recipient"] as? Map<*, *>
                        val bodyMap = message["body"] as? Map<*, *>

                        val userId = (sender?.get("user_id") as? Number)?.toString() ?: return@forEach
                        val chatId = (recipient?.get("chat_id") as? Number)?.toString() ?: return@forEach
                        val text = bodyMap?.get("text") as? String

                        // ✅ Парсим контакт
                        val attachments = bodyMap?.get("attachments") as? List<*>
                        attachments?.forEach { attachment ->
                            val attachmentMap = attachment as? Map<*, *>
                            if (attachmentMap?.get("type") == "contact") {
                                val payload = attachmentMap["payload"] as? Map<*, *>
                                val vcfInfo = payload?.get("vcf_info") as? String
                                log.info("📱 [Auth Bot] Получен контакт: $vcfInfo")

                                if (vcfInfo != null) {
                                    val contactInfo = parseContactFromVcf(vcfInfo)
                                    log.info("📱 [Auth Bot] Найден контакт: phone=${contactInfo?.phone}, name=${contactInfo?.name}")
                                    if (contactInfo != null) {
                                        handlePhoneNumberReceived(userId, chatId, contactInfo)
                                    }
                                }
                            }
                        }

                        log.info("📩 [Auth Bot] Получено сообщение от user_id=$userId: $text")

                        if (text != null && text.isNotBlank()) {
                            handleAuthMessage(userId, chatId, text)
                        }
                    }

                    "message_callback" -> {
                        val callbackData = update["callback"] as? Map<*, *> ?: return@forEach
                        val userData = callbackData["user"] as? Map<*, *> ?: return@forEach
                        val userId = (userData["user_id"] as? Number)?.toString() ?: return@forEach
                        val payload = callbackData["payload"] as? String ?: return@forEach

                        val message = update["message"] as? Map<*, *> ?: return@forEach
                        val recipient = message["recipient"] as? Map<*, *> ?: return@forEach
                        val chatId = (recipient["chat_id"] as? Number)?.toString() ?: return@forEach

                        log.info("🔘 [Auth Bot] Callback от user_id=$userId, payload=$payload")
                        handleAuthCallback(userId, chatId, payload)
                    }

                    else -> {
                        log.debug("⚠️ [Auth Bot] Неизвестный тип: $updateType")
                    }
                }
            }

        } catch (_: ResourceAccessException) {
            // таймаут — нормально
        } catch (e: Exception) {
            log.error("❌ [Auth Bot] Ошибка в Long Polling", e)
        }
    }

    private fun parseContactFromVcf(vcfInfo: String): ContactInfo? {
        return try {
            val vcard = Ezvcard.parse(vcfInfo).first()
            val phone = vcard.telephoneNumbers.firstOrNull()?.text
            val name = vcard.formattedName?.value ?: "Пользователь"

            if (phone != null) {
                ContactInfo(phone = phone, name = name)
            } else {
                null
            }
        } catch (e: Exception) {
            log.error("❌ [Auth Bot] Ошибка парсинга VCF: ${e.message}")
            null
        }
    }

    data class ContactInfo(
        val phone: String,
        val name: String
    )

    private fun sendMainMenu(userId: String, chatId: String) {
        val buttons = listOf(
            listOf(mapOf("type" to "callback", "text" to "📝 Регистрация", "payload" to "register")),
            listOf(mapOf("type" to "request_contact", "text" to "📱 Поделиться номером")),
            listOf(mapOf("type" to "callback", "text" to "ℹ️ Помощь", "payload" to "auth_help"))
        )

        val text = """
            👋 *Добро пожаловать в бот авторизации!*
            
            Нажмите *"Регистрация"* или *"Поделиться номером"*.
        """.trimIndent()

        // ✅ Используем chatId!
        botAuthClient.sendMessageWithKeyboard(chatId, text, buttons, "markdown")
    }

    private fun handleAuthMessage(userId: String, chatId: String, text: String) {
        val trimmedText = text.trim()

        when (trimmedText.lowercase()) {
            "/start" -> sendMainMenu(userId, chatId)
            "/help" -> {
                // ✅ Используем chatId!
                botAuthClient.sendMessage(
                    chatId,
                    "📖 /start — главное меню",
                    "markdown"
                )
            }
            else -> {
                if (trimmedText.isNotEmpty()) {
                    // ✅ Используем chatId!
                    botAuthClient.sendMessage(
                        chatId,
                        "❌ Нажмите *Регистрация* или *Поделиться номером*",
                        "markdown"
                    )
                }
            }
        }
    }

    private fun handlePhoneNumberReceived(
        userId: String,
        chatId: String,
        contact: ContactInfo
    ) {
        val cleanNumber = contact.phone.replace(Regex("[^\\d+]"), "")
        val name = contact.name

        log.info("📱 [Auth Bot] ✅ ПОЛУЧЕН КОНТАКТ: userId=$userId, chatId=$chatId, phone=$cleanNumber, name=$name")

        // ✅ Ищем номер в базе
        val searchResults = agnPhoneService.searchByPhone(cleanNumber)
        log.info("📱 [Auth Bot] Найдено записей: ${searchResults.size}")

        // Формируем сообщение с результатами поиска
        val messageText = buildSearchResultMessage(name, cleanNumber, searchResults)

        val buttons = listOf(
            listOf(
                mapOf(
                    "type" to "callback",
                    "text" to "✅ Подтвердить",
                    "payload" to "confirm_phone_${cleanNumber}_${name}"
                )
            ),
            listOf(
                mapOf(
                    "type" to "callback",
                    "text" to "◀️ Назад",
                    "payload" to "back_to_menu"
                )
            )
        )

        botAuthClient.sendMessageWithKeyboard(chatId, messageText, buttons, "markdown")
    }

    /**
     * Формируем сообщение с результатами поиска
     */
    private fun buildSearchResultMessage(
        name: String,
        phone: String,
        results: List<AgnphonenumberlistDto>
    ): String {
        val sb = StringBuilder()

        sb.append("✅ *Контакт получен!*\n\n")
        sb.append("👤 *Имя:* $name\n")
        sb.append("📱 *Номер:* `$phone`\n\n")

        if (results.isNotEmpty()) {
            sb.append("📋 *Найдено в базе:* ${results.size} записей\n\n")
            results.forEachIndexed { index, item ->
                sb.append("${index + 1}. *${item.agnname}*\n")
                sb.append("   📧 ${item.email ?: "нет email"}\n")
                sb.append("   📱 ${item.phonenumber ?: "нет номера"}\n")
                item.agncode?.let { sb.append("   📋 Код: $it\n") }
                sb.append("\n")
            }
        } else {
            sb.append("⚠️ *Номер не найден в базе*\n")
            sb.append("Возможно, требуется регистрация.\n\n")
        }

        sb.append("Подтвердите, если данные верные.")

        return sb.toString()
    }

    private fun handleAuthCallback(userId: String, chatId: String, payload: String) {
        log.info("🔘 [Auth Bot] Callback: userId=$userId, chatId=$chatId, payload=$payload")

        when {
            payload == "register" -> {
                log.info("📝 [Auth Bot] Пользователь $userId нажал 'Регистрация'")
                // ✅ Используем chatId!
                botAuthClient.sendMessage(
                    chatId,
                    "📝 Нажмите *Поделиться номером* для отправки контакта.",
                    "markdown"
                )
            }

            payload == "auth_help" -> {
                // ✅ Используем chatId!
                botAuthClient.sendMessage(
                    chatId,
                    "📖 Нажмите *Регистрация* или *Поделиться номером*",
                    "markdown"
                )
            }

            payload == "back_to_menu" -> {
                sendMainMenu(userId, chatId)
            }

            payload.startsWith("confirm_phone_") -> {
                val parts = payload.removePrefix("confirm_phone_").split("_")
                val phoneNumber = parts.getOrNull(0) ?: ""
                val name = parts.drop(1).joinToString("_")

                log.info("✅ [Auth Bot] Пользователь $userId подтвердил: name=$name, phone=$phoneNumber")

                // ✅ Используем chatId!
                botAuthClient.sendMessage(
                    chatId,
                    """
                    ✅ *Регистрация успешна!*
                    
                    👤 *Имя:* $name
                    📱 *Номер:* `$phoneNumber`
                    
                    🔐 Добро пожаловать!
                    """.trimIndent(),
                    "markdown"
                )
            }

            else -> {
                log.warn("⚠️ [Auth Bot] Неизвестный payload: $payload")
            }
        }
    }
}