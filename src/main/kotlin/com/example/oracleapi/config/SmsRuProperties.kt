package com.example.oracleapi.config

import jakarta.validation.constraints.*
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "sms.ru")
class SmsRuProperties {

    @field:NotBlank(message = "API ID не может быть пустым")
    var apiId: String = ""

    var defaultFrom: String? = null

    var testMode: Boolean = false

    @field:Min(1000)
    @field:Max(60000)
    var connectTimeout: Int = 30000

    @field:Min(1000)
    @field:Max(60000)
    var readTimeout: Int = 30000

    @field:Min(1)
    @field:Max(5000)
    var maxNumbersPerRequest: Int = 100

    override fun toString(): String {
        return "SmsRuProperties(apiId=${apiId.take(8)}..., defaultFrom=$defaultFrom, testMode=$testMode)"
    }
}