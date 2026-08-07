package com.example.oracleapi.dto.max.auth

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class VerifyCodeRequestDto(
    @field:NotBlank(message = "Номер телефона обязателен")
    val phone: String,

    @field:NotBlank(message = "Код обязателен")
    @field:Size(min = 4, max = 4, message = "Код должен состоять из 4 цифр")
    val code: String
)
