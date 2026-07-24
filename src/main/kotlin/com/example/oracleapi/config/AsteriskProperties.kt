package com.example.oracleapi.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "asterisk.ami")
class AsteriskProperties {
    var host: String = ""
    var port: Int = 5038
    var username: String = ""
    var secret: String = ""
}