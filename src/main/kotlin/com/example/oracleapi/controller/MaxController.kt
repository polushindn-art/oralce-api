package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.max.AgeVerificationRequest
import com.example.oracleapi.dto.max.AgeVerificationResponse
import com.example.oracleapi.dto.max.MessageRequest
import com.example.oracleapi.dto.max.MessageResponse
import com.example.oracleapi.service.max.verification.MaxVerificationService
import com.example.oracleapi.service.max.call.MessageService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/max")
@Tag(name = "MAX", description = "Интеграция с Цифровым ID MAX")
class MaxController(
    private val verificationService: MaxVerificationService,
    private val messageService: MessageService
) : BaseController() {

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
        val result = messageService.sendMessage(
            chatId = request.chatId,
            text = request.text,
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
        val info = messageService.getBotInfo()
        return success(info, "Информация о боте получена")
    }

}