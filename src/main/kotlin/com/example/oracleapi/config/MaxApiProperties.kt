package com.example.oracleapi.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "max.api")
class MaxApiProperties {
    var verificationToken: String = "Lwc9r3qpr7YkVp1Tov-36lGPjOd3ICKMRmkLVeC04E7vSRZ1vJVjp30LQxGHh8doT4SsSBuyySA"
    var ageVerificationUrl: String = "https://ext-api2.max.ru/v2/business/pos/age-verification"
    var connectTimeout: Int = 5000
    var readTimeout: Int = 10000
}