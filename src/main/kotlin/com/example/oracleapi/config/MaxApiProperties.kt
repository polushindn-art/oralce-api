package com.example.oracleapi.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "max.api")
class MaxApiProperties {
    // Токен для Цифрового ID (проверка возраста)
    var verificationToken: String = ""
    var ageVerificationUrl: String = "https://ext-api2.max.ru/v2/business/pos/age-verification"

    // Общий URL
    var botApiUrl: String = "https://platform-api2.max.ru"

    // Токен для бота (отправка сообщений)
    var botCallToken: String = ""

    // Токен для отправки уведомлений контрагентам
    var botAuthToken: String = ""

    // Общие настройки
    var connectTimeout: Int = 5000
    var readTimeout: Int = 60000
}