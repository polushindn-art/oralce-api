package com.example.oracleapi.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "max.api")
class MaxApiProperties {
    // Токен для Цифрового ID (проверка возраста)
    var verificationToken: String = "Lwc9r3qpr7YkVp1Tov-36lGPjOd3ICKMRmkLVeC04E7vSRZ1vJVjp30LQxGHh8doT4SsSBuyySA"
    var ageVerificationUrl: String = "https://ext-api2.max.ru/v2/business/pos/age-verification"

    // Токен для бота (отправка сообщений)
    var botToken: String = "f9LHodD0cOKenCqRJ4ITIZisdC-YlxxC9k8ctF2r9USZ2D4J4imjggDy7dnM-e0rO3FzonqbBVjmFbhzzKrK"
    var botApiUrl: String = "https://platform-api2.max.ru"

    // Общие настройки
    var connectTimeout: Int = 5000
    var readTimeout: Int = 60000
}