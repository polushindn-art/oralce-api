package com.example.oracleapi.dto.max.auth

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AuthRequestDto(
    @field:NotBlank(message = "Номер телефона обязателен")
    @field:Size(max = 50, message = "Номер телефона не должен превышать 50 символов")
    val phone: String,

    @field:Size(max = 50)
    val botType: String = "MAIN"
)