package com.example.oracleapi.util

import org.springframework.stereotype.Component

@Component
object BotButtons {

    /**
     * Кнопка "◀️ В меню"
     */
    fun menuButton(): List<List<Map<String, Any>>> {
        return listOf(
            listOf(
                mapOf(
                    "type" to "callback",
                    "text" to "◀️ В меню",
                    "payload" to "back_to_menu"
                )
            )
        )
    }

    /**
     * Кнопки для регистрации
     */
    fun registerButtons(): List<List<Map<String, Any>>> {
        return listOf(
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
    }

    /**
     * Кнопки для помощи
     */
    fun helpButtons(): List<List<Map<String, Any>>> {
        return listOf(
            listOf(
                mapOf(
                    "type" to "callback",
                    "text" to "◀️ В меню",
                    "payload" to "back_to_menu"
                )
            )
        )
    }

    /**
     * Кнопки для подтверждения
     */
    fun confirmButtons(): List<List<Map<String, Any>>> {
        return listOf(
            listOf(
                mapOf(
                    "type" to "callback",
                    "text" to "✅ Подтвердить",
                    "payload" to "confirm"
                )
            ),
            listOf(
                mapOf(
                    "type" to "callback",
                    "text" to "◀️ В меню",
                    "payload" to "back_to_menu"
                )
            )
        )
    }

    /**
     * Кнопки для отписки
     */
    fun unsubscribeButtons(): List<List<Map<String, Any>>> {
        return listOf(
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
    }

    /**
     * Кнопка перезвона
     */
    fun callMobileButton(internalNumber: String, callerNumber: String): List<List<Map<String, Any>>> {
        return listOf(
            listOf(
                mapOf(
                    "type" to "callback",
                    "text" to "📱 Попросить перезвонить мне на сотовый",
                    "payload" to "call_mobile_${internalNumber}_$callerNumber"
                )
            )
        )
    }

    /**
     * Кнопка для регистрации снова
     */
    fun registerAgainButton(): List<List<Map<String, Any>>> {
        return listOf(
            listOf(
                mapOf(
                    "type" to "callback",
                    "text" to "📝 Зарегистрироваться снова",
                    "payload" to "register"
                )
            )
        )
    }

}