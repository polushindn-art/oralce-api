package com.example.oracleapi.service.max.mainBoth

import com.example.oracleapi.Helper
import com.example.oracleapi.config.MaxApiProperties
import com.example.oracleapi.service.agnphonenumber.AgnPhoneService
import com.example.oracleapi.service.barcode.BarcodeService
import com.example.oracleapi.service.maxUserAgn.MaxUserAgnService
import com.example.oracleapi.service.nomnlist.NomnlistService
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
import kotlin.text.takeLast

@Service
class MaxMainPollingService(
    private val restTemplate: RestTemplate,
    private val properties: MaxApiProperties,
    private val botClient: MaxBotMainClient,
    private val agnPhoneService: AgnPhoneService,
    private val maxUserAgnService: MaxUserAgnService,
    private val barcodeService: BarcodeService,
    private val nomnlistService: NomnlistService
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
                log.info("🤖 [Main Bot] Получены новые обновления. Маркер: $marker")
            }

            updates.forEach { updateRaw ->
                val update = updateRaw as? Map<*, *> ?: return@forEach

                when (val updateType = update["update_type"] as? String) {
                    "bot_started" -> {
                        val userId = (update["user_id"] as? Number)?.toString() ?: return@forEach
                        val chatId = (update["chat_id"] as? Number)?.toString() ?: return@forEach
                        log.info("🚀 [Main Bot] Бот запущен: userId=$userId, chatId=$chatId")
                        sendMainMenu(chatId)
                    }

                    "message_created" -> {
                        val message = update["message"] as? Map<*, *> ?: return@forEach
                        val sender = message["sender"] as? Map<*, *>
                        val recipient = message["recipient"] as? Map<*, *>
                        val bodyMap = message["body"] as? Map<*, *>

                        val userId = (sender?.get("user_id") as? Number)?.toString() ?: return@forEach
                        val chatId = (recipient?.get("chat_id") as? Number)?.toString() ?: return@forEach
                        val text = bodyMap?.get("text") as? String

                        // Проверяем вложения
                        val attachments = bodyMap?.get("attachments") as? List<*>
                        attachments?.forEach { attachment ->
                            val attachmentMap = attachment as? Map<*, *>
                            when (attachmentMap?.get("type")) {
                                "contact" -> {
                                    // ✅ Обработка контакта
                                    val payload = attachmentMap["payload"] as? Map<*, *>
                                    val vcfInfo = payload?.get("vcf_info") as? String
                                    log.info("📱 [Main Bot] Получен контакт: $vcfInfo")

                                    if (vcfInfo != null) {
                                        val contactInfo = parseContactFromVcf(vcfInfo)
                                        if (contactInfo != null) {
                                            handlePhoneNumberReceived(userId, chatId, contactInfo)
                                        }
                                    }
                                }

                                "image" -> {
                                    val payload = attachmentMap["payload"] as? Map<*, *>
                                    val photoUrl = payload?.get("url") as? String
                                    val photoToken = payload?.get("token") as? String

                                    log.info("📸 [Main Bot] Получено ФОТО от user_id=$userId, chatId=$chatId")
                                    log.info("📸 [Main Bot] URL: $photoUrl")
                                    log.info("📸 [Main Bot] TOKEN: $photoToken")

                                    // ✅ Используем тот же токен, что и для получения обновлений
                                    if (photoUrl != null) {
                                        val authToken = "Bearer ${properties.botAuthToken}"
                                        val barcode = barcodeService.decodeBarcodeFromUrlWithAuth(photoUrl, authToken)
                                        if (barcode != null) {
                                            log.info("📱 [Main Bot] Распознан код: $barcode")
                                            val nomen = nomnlistService.findByBarcode(barcode)

                                            val article = Helper.insertSpaceToArticle(nomen?.article ?: "не найден")


                                            val nomenText = """
                                                ${nomen?.nomenname ?: "Товар со штрих-кодом $barcode не найден"}
                                                Артикул: $article
                                            """.trimIndent()

                                            botClient.sendMessage(
                                                chatId,
                                                """
                                                    $nomenText
                                                    """.trimIndent(),
                                                "markdown"
                                            )
                                        } else {
                                            botClient.sendMessage(
                                                chatId,
                                                "❌ Не удалось распознать код на фото",
                                                "markdown"
                                            )
                                        }
                                    } else {
                                        botClient.sendMessage(
                                            chatId,
                                            "📸 *Фото получено*",
                                            "markdown"
                                        )
                                    }
                                }
                            }
                        }

                        log.info("📩 [Main Bot] Получено сообщение от user_id=$userId: $text")

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

                        log.info("🔘 [Main Bot] Callback от user_id=$userId, payload=$payload")
                        handleAuthCallback(userId, chatId, payload)
                    }

                    else -> {
                        log.debug("⚠️ [Main Bot] Неизвестный тип: $updateType")
                    }
                }
            }

        } catch (_: ResourceAccessException) {
            // таймаут — нормально
        } catch (e: Exception) {
            log.error("❌ [Main Bot] Ошибка в Long Polling", e)
        }
    }

    private fun parseContactFromVcf(vcfInfo: String): ContactInfo? {
        return try {
            val vcard = Ezvcard.parse(vcfInfo).first()
            val phone = vcard.telephoneNumbers.firstOrNull()?.text
            val name = vcard.formattedName?.value ?: "Пользователь"
            if (phone != null) ContactInfo(phone = phone, name = name) else null
        } catch (e: Exception) {
            log.error("❌ [Main Bot] Ошибка парсинга VCF: ${e.message}")
            null
        }
    }

    data class ContactInfo(val phone: String, val name: String)

    private fun sendMainMenu(chatId: String) {
        val isRegistered = maxUserAgnService.isChatRegistered(chatId)

        val buttons = if (isRegistered) {
            listOf(
                listOf(
                    mapOf(
                        "type" to "callback",
                        "text" to "📋 Мои данные",
                        "payload" to "my_data"
                    )
                ),
                listOf(
                    mapOf(
                        "type" to "callback",
                        "text" to "🗑️ Отписаться",
                        "payload" to "unsubscribe"
                    )
                ),
                listOf(
                    mapOf(
                        "type" to "callback",
                        "text" to "ℹ️ Помощь",
                        "payload" to "auth_help"
                    )
                )
            )
        } else {
            listOf(
                listOf(
                    mapOf(
                        "type" to "callback",
                        "text" to "📝 Регистрация",
                        "payload" to "register"
                    )
                ),
                listOf(
                    mapOf(
                        "type" to "callback",
                        "text" to "ℹ️ Помощь",
                        "payload" to "auth_help"
                    )
                )
            )
        }

        val text = if (isRegistered) {
            """
            👋 *Добро пожаловать!*
            
            Вы уже зарегистрированы.
            
            Выберите действие в меню ниже.
            """.trimIndent()
        } else {
            """
            👋 *Добро пожаловать!*
            
            Для начала работы необходимо зарегистрироваться.
            
            Нажмите *"Регистрация"* для начала.
            """.trimIndent()
        }

        botClient.sendMessageWithKeyboard(chatId, text, buttons, "markdown")
    }

    private fun sendHelp(chatId: String) {
        val buttons = listOf(
            listOf(
                mapOf(
                    "type" to "callback",
                    "text" to "◀️ В меню",
                    "payload" to "back_to_menu"
                )
            )
        )

        botClient.sendMessageWithKeyboard(
            chatId,
            """
            📖 *Помощь*
            
            🔐 *Как зарегистрироваться:*
            1. Нажмите *Регистрация* в главном меню
            2. Нажмите *Поделиться номером*
            3. Подтвердите отправку номера
            4. Дождитесь сообщения об успешной регистрации
            
            👤 *Кто может зарегистрироваться:*
            Клиенты магазина.
            Номер телефона должен совпадать с номером, с которым вы зарегистрированы в MAX.
            
            🛒 *Как стать клиентом:*
            Обратитесь в информационную службу.
                        
            📌 *Команды:*
            /start — главное меню
            /help — эта справка
            """.trimIndent(),
            buttons,
            "markdown"
        )
    }

    private fun handleAuthMessage(userId: String, chatId: String, text: String) {
        when (text.trim().lowercase()) {
            "/start" -> {
                log.info("📌 [Main Bot] Пользователь $userId вызвал /start")
                sendMainMenu(chatId)
            }

            "/help" -> {
                log.info("📌 [Main Bot] Пользователь $userId вызвал /help")
                sendHelp(chatId)
            }

            else -> {
                log.info("📌 [Main Bot] Неизвестная команда от $userId: $text, показываем меню")
                sendMainMenu(chatId)
            }
        }
    }

    private fun handlePhoneNumberReceived(userId: String, chatId: String, contact: ContactInfo) {
        // ✅ Проверяем, зарегистрирован ли уже чат
        if (maxUserAgnService.isChatRegistered(chatId)) {
            val buttons = listOf(
                listOf(
                    mapOf(
                        "type" to "callback",
                        "text" to "◀️ В меню",
                        "payload" to "back_to_menu"
                    )
                )
            )
            botClient.sendMessageWithKeyboard(
                chatId,
                "ℹ️ *Вы уже зарегистрированы!*",
                buttons,
                "markdown"
            )
            return
        }

        val cleanNumber = contact.phone.replace(Regex("[^\\d+]"), "")
        val nameFromMax = contact.name

        log.info("📱 [Main Bot] ✅ ПОЛУЧЕН КОНТАКТ: userId=$userId, chatId=$chatId, phone=$cleanNumber, nameFromMax=$nameFromMax")

        // 🔍 Проверяем номер в таблице контрагентов
        val searchResults = agnPhoneService.searchByPhone(cleanNumber)

        if (searchResults.isEmpty()) {
            // ❌ Номер не найден в базе
            val buttons = listOf(
                listOf(
                    mapOf(
                        "type" to "callback",
                        "text" to "◀️ В меню",
                        "payload" to "back_to_menu"
                    )
                )
            )
            botClient.sendMessageWithKeyboard(
                chatId,
                """
                ❌ *Клиент с номером `$cleanNumber` не найден!*
                
                Пожалуйста, проверьте правильность номера или обратитесь к администратору.
                """.trimIndent(),
                buttons,
                "markdown"
            )
            return
        }

        // ✅ Номер найден — регистрируем
        try {
            maxUserAgnService.addUserAgn(
                userId = userId,
                chatId = chatId,
                phone = cleanNumber,
                botType = "MAIN",
                userName = nameFromMax
            )

            val buttons = listOf(
                listOf(
                    mapOf(
                        "type" to "callback",
                        "text" to "◀️ В меню",
                        "payload" to "back_to_menu"
                    )
                )
            )

            botClient.sendMessageWithKeyboard(
                chatId,
                """
                👋 *Привет, $nameFromMax!*
                
                ✅ Регистрация прошла успешно!
                
                📱 Ваш номер: `$cleanNumber`
                
                🔐 Добро пожаловать!
                """.trimIndent(),
                buttons,
                "markdown"
            )

        } catch (e: Exception) {
            log.error("❌ [Main Bot] Ошибка регистрации", e)
            val buttons = listOf(
                listOf(
                    mapOf(
                        "type" to "callback",
                        "text" to "◀️ В меню",
                        "payload" to "back_to_menu"
                    )
                )
            )
            botClient.sendMessageWithKeyboard(
                chatId,
                "❌ Ошибка регистрации. Попробуйте позже.",
                buttons,
                "markdown"
            )
        }
    }

    private fun handleAuthCallback(userId: String, chatId: String, payload: String) {
        log.info("🔘 [Main Bot] Callback: userId=$userId, chatId=$chatId, payload=$payload")

        when {
            payload == "register" -> {
                if (maxUserAgnService.isChatRegistered(chatId)) {
                    val buttons = listOf(
                        listOf(
                            mapOf(
                                "type" to "callback",
                                "text" to "◀️ В меню",
                                "payload" to "back_to_menu"
                            )
                        )
                    )
                    botClient.sendMessageWithKeyboard(
                        chatId,
                        "ℹ️ *Вы уже зарегистрированы!*",
                        buttons,
                        "markdown"
                    )
                    return
                }

                log.info("📝 [Main Bot] Пользователь $userId нажал 'Регистрация'")

                val buttons = listOf(
                    listOf(
                        mapOf(
                            "type" to "request_contact",
                            "text" to "📱 Поделиться номером"
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

                botClient.sendMessageWithKeyboard(
                    chatId,
                    """
                    📝 *Регистрация*
                    
                    Нажмите *"Поделиться номером"* для отправки контакта.
                    
                    ⚠️ *Важно:*
                    Вы должны быть клиентом магазина.
                    Номер в боте MAX должен совпадать с номером в системе.
                    Если номер не найден, регистрация будет отклонена.
                    Для получения карты обратитесь в информационную службу.
                    """.trimIndent(),
                    buttons,
                    "markdown"
                )
            }

            payload == "my_data" -> {
                val userAgn = maxUserAgnService.findByChatId(chatId)
                if (userAgn != null) {
                    val buttons = listOf(
                        listOf(
                            mapOf(
                                "type" to "callback",
                                "text" to "◀️ В меню",
                                "payload" to "back_to_menu"
                            )
                        )
                    )
                    botClient.sendMessageWithKeyboard(
                        chatId,
                        """
                        📋 *Ваши данные:*
                        
                        📱 Номер: `${userAgn.phone}`
                        👤 Имя: ${userAgn.userName ?: "не указано"}
                        📅 Зарегистрирован: ${userAgn.createdAt}
                        """.trimIndent(),
                        buttons,
                        "markdown"
                    )
                } else {
                    sendMainMenu(chatId)
                }
            }

            payload == "unsubscribe" -> {
                val buttons = listOf(
                    listOf(
                        mapOf(
                            "type" to "callback",
                            "text" to "✅ Да, отписаться",
                            "payload" to "confirm_unsubscribe"
                        )
                    ),
                    listOf(
                        mapOf(
                            "type" to "callback",
                            "text" to "❌ Нет, остаться",
                            "payload" to "back_to_menu"
                        )
                    )
                )

                botClient.sendMessageWithKeyboard(
                    chatId,
                    """
                    🗑️ *Отписка*
                    
                    Вы уверены, что хотите отписаться?
                    
                    После отписки вы больше не будете получать:
                    • Коды для подтверждения на кассе
                    • Уведомления о заказах
                    • Другие сообщения от бота
                    
                    Вы всегда сможете зарегистрироваться снова.
                    """.trimIndent(),
                    buttons,
                    "markdown"
                )
            }

            payload == "confirm_unsubscribe" -> {
                try {
                    maxUserAgnService.deleteByChatId(chatId)

                    log.info("🗑️ [Main Bot] Пользователь $chatId отписался")

                    val buttons = listOf(
                        listOf(
                            mapOf(
                                "type" to "callback",
                                "text" to "📝 Зарегистрироваться снова",
                                "payload" to "register"
                            )
                        )
                    )

                    botClient.sendMessageWithKeyboard(
                        chatId,
                        """
                        ✅ *Вы отписались!*
                        
                        Вы больше не будете получать уведомления.
                        
                        Если захотите вернуться, нажмите *"Зарегистрироваться снова"*.
                        """.trimIndent(),
                        buttons,
                        "markdown"
                    )

                    sendMainMenu(chatId)

                } catch (e: Exception) {
                    log.error("❌ [Main Bot] Ошибка отписки", e)
                    botClient.sendMessage(
                        chatId,
                        "❌ Ошибка при отписке. Попробуйте позже.",
                        "markdown"
                    )
                    sendMainMenu(chatId)
                }
            }

            payload == "auth_help" -> {
                sendHelp(chatId)
            }

            payload == "back_to_menu" -> {
                sendMainMenu(chatId)
            }

            else -> {
                log.warn("⚠️ [Main Bot] Неизвестный payload: $payload")
                sendMainMenu(chatId)
            }
        }
    }
}