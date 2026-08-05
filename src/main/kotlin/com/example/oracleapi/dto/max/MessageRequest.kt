package com.example.oracleapi.dto.max

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Запрос на отправку сообщения от бота")
data class MessageRequest(
    @field:Schema(description = "Chat ID получателя", example = "123456789")
    val chatId: String,

    @field:Schema(description = "Текст сообщения", example = "*Заказ #12345*\n━━━━━━━━━━━━━━━━\n *Дата:* 04.08.2026\n*Сумма:* 5 000 ₽\n*Статус:* ✅ *Доставлен*\n━━━━━━━━━━━━━━━━\n*Адрес:* ул. Примерная, д. 1\n*Получатель:* Иванов И.")
    val text: String,

    @field:Schema(description = "Формат: markdown или html", example = "markdown")
    val format: String = "markdown"
)