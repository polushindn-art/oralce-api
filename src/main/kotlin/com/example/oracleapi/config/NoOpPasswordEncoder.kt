package com.example.oracleapi.config

import org.springframework.security.crypto.password.PasswordEncoder

class NoOpPasswordEncoder: PasswordEncoder {

    override fun encode(rawPassword: CharSequence?): String {
        return rawPassword.toString()
    }

    override fun matches(rawPassword: CharSequence?, encodedPassword: String?): Boolean {
        return rawPassword.toString() == encodedPassword
    }
}