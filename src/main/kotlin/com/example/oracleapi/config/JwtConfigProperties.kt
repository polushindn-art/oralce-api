package com.example.oracleapi.config
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")

class JwtConfigProperties {
    var secret: String = ""
        set(value) {
            require(value.isNotBlank() && value.length >= 32) {
                "JWT secret key must be at least 32 characters long"
            }
            field = value
        }

    var expiration: Long = 36000000  // public property!
        set(value) {
            require(value > 0) { "Expiration must be positive" }
            field = value
        }

    var secure: Boolean = false  // public property!

    var cookieName: String = "ARS_AUTH_TOKEN"  // public property!

    val cookieMaxAgeSeconds: Int
        get() = (expiration / 1000).toInt()


}