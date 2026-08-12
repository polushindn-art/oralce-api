package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.max.AgeVerificationRequest
import com.example.oracleapi.dto.max.AgeVerificationResponse
import com.example.oracleapi.dto.max.MessageRequest
import com.example.oracleapi.dto.max.MessageResponse
import com.example.oracleapi.dto.max.SendByPhoneRequest
import com.example.oracleapi.dto.max.auth.AuthCodeResponse
import com.example.oracleapi.dto.max.auth.AuthRequestDto
import com.example.oracleapi.dto.maxUsertAgn.MaxUserAgnDto
import com.example.oracleapi.service.agnphonenumber.AgnPhoneService
import com.example.oracleapi.service.max.RegistrationCodeService
import com.example.oracleapi.service.max.mainBoth.MainBotMessageService
import com.example.oracleapi.service.max.verification.MaxVerificationService
import com.example.oracleapi.service.maxUserAgn.MaxUserAgnService
import com.example.oracleapi.util.BotButtons
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/max")
@Tag(name = "MAX", description = "Боты MAX")
class MaxController(
    private val verificationService: MaxVerificationService,
    private  val mainBotMessageService: MainBotMessageService,
    private val agnPhoneService: AgnPhoneService,
    private val maxUserAgnService: MaxUserAgnService,
    private val registrationCodeService: RegistrationCodeService
) : BaseController() {

    private val log = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/age")
    @Operation(summary = "Проверить возраст через Цифровой ID")
    fun verifyAge(
        @Valid @RequestBody request: AgeVerificationRequest
    ): MyApiResponse<AgeVerificationResponse> {
        val result = verificationService.verifyAge(request.sessionId)

        return if (result.error == null) {
            val message = if (result.isAdult) "Возраст подтвержден (18+)" else "Возраст не подтвержден"
            success(result, message)
        } else {
            error(result.error, result)
        }
    }

    @PostMapping("/send")
    @Operation(
        summary = "Отправить сообщение от бота",
        description = "Отправляет сообщение пользователю или в группу в MAX"
    )
    fun sendMessage(
        @Valid @RequestBody request: MessageRequest
    ): MyApiResponse<MessageResponse> {


        val result = mainBotMessageService.sendMessageWithInlineKeyboard(
            chatId = request.chatId,
            text = request.text,
            buttons = BotButtons.menuButton(),
            format = request.format
        )

        return if (result.success) {
            success(result, "Сообщение отправлено")
        } else {
            error(result.error ?: "Неизвестная ошибка", result)
        }
    }

    @GetMapping("/info")
    @Operation(
        summary = "Получить информацию о боте",
        description = "Возвращает данные бота: user_id, first_name, username, is_bot, description и др."
    )
    fun getBotInfo(): MyApiResponse<Map<String, Any>> {
        val info = mainBotMessageService.getBotInfo()
        return success(info, "Информация о боте получена")
    }

    @PostMapping("/code_request")
    @Operation(
        summary = "Авторизация",
        description = "Отправить код авторизации в Маск"
    )
    fun requestCodeForCashier(
        @RequestBody request: AuthRequestDto
    ): MyApiResponse<AuthCodeResponse> {
        val phone = request.phone.replace(Regex("[^\\d+]"), "")

        // 1. Проверяем номер в базе
        val searchResults = agnPhoneService.searchByPhone(phone)
        if (searchResults.isEmpty()) {
            return error(
                "Номер $phone не найден в системе",
                AuthCodeResponse(success = false, message = "Номер не найден")
            )
        }

        // 2. Проверяем, есть ли chat_id
        val chatId = maxUserAgnService.findChatIdByPhoneAndBotType(phone, request.botType) ?: return error(
            "Покупатель с номером $phone не найден в боте",
            AuthCodeResponse(success = false, message = "Покупатель не авторизован")
        )

        // 3. Генерируем код
        val code = (1000..9999).random().toString()

        // 4. ✅ ПЫТАЕМСЯ ОТПРАВИТЬ и ПРОВЕРЯЕМ РЕЗУЛЬТАТ
        val sendResult = sendCodeToBot(chatId, code)
        if (!sendResult) {
            return error(
                "Не удалось отправить код покупателю",
                AuthCodeResponse(success = false, message = "Ошибка отправки")
            )
        }

        // 5. Сохраняем код (ТОЛЬКО ПОСЛЕ УСПЕШНОЙ ОТПРАВКИ!)
        //registrationCodeService.saveCode(code, phone, "MAIN")

        return success(
            AuthCodeResponse(
                success = true,
                code = code,
                phone = phone,
                message = "Код отправлен покупателю"
            ),
            "Код сгенерирован"
        )
    }

    /**
     * Отправить код в бот
     * @return true - успешно, false - ошибка
     */
    private fun sendCodeToBot(chatId: String, code: String): Boolean {
        val response = mainBotMessageService.sendMessageWithInlineKeyboard(
            chatId = chatId,
            text = """
        🔐 *Ваш код подтверждения:*
        
        # **`$code`** #
        
        Назовите этот код кассиру.
        """.trimIndent(),
            BotButtons.menuButton(),
            format = "markdown"
        )

        // ✅ Проверяем success!
        if (response.success) {
            log.info("✅ [Auth] Код отправлен в чат $chatId")
            return true
        } else {
            log.error("❌ [Auth] Ошибка отправки кода: ${response.error}")
            return false
        }
    }

    @PostMapping("/send-by-phone")
    @Operation(
        summary = "Отправить сообщение по номеру телефона",
        description = "Находит chat_id по номеру телефона и отправляет сообщение с кнопкой 'В меню'"
    )
    fun sendMessageByPhone(
        @Valid @RequestBody request: SendByPhoneRequest
    ): MyApiResponse<MessageResponse> {
        log.info("📤 [API] Отправка сообщения по номеру: ${request.phone}")

        // 1. Находим chat_id по номеру телефона
        val chatId = maxUserAgnService.findChatIdByPhoneAndBotType(request.phone, "MAIN") ?: return error(
            "Покупатель с номером ${request.phone} не найден",
            MessageResponse(success = false, message = "Покупатель не найден")
        )

        // 2. Отправляем сообщение
        val result = mainBotMessageService.sendMessageWithInlineKeyboard(
            chatId = chatId,
            text = request.text,
            buttons = BotButtons.menuButton(),
            format = request.format
        )

        return if (result.success) {
            success(result, "Сообщение отправлено на номер ${request.phone}")
        } else {
            error(result.error ?: "Неизвестная ошибка", result)
        }
    }

    @GetMapping("/status-main")
    @Operation(
        description = "Проверяет, зарегистрирован ли номер в MAIN боте",
        summary = "Статус номера в MAIN боте"
    )
    fun getPhoneStatusMain(@Valid phone: String): MyApiResponse<MaxUserAgnDto?> {
        val result = maxUserAgnService.findMainUserByPhone(phone)

        return if (result != null) {
            success(result, "Номер зарегистрирован в MAIN боте")
        } else {
            success(result, "Номер не зарегистрирован в MAIN боте")
        }
    }

}