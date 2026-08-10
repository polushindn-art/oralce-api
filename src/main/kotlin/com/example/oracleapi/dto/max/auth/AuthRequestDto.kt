package com.example.oracleapi.dto.max.auth

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AuthRequestDto(
    @field:NotBlank(message = "Номер телефона обязателен")
    @field:Size(max = 50, message = "Номер телефона не должен превышать 50 символов")
    @field:Schema(description = "Номер зарегистрированного пользователя как нашего контрагента", required = true)
    val phone: String,

    @field:Schema(description = "Тип бота", required = false)
    @field:Size(max = 50)
    val botType: String = "MAIN"
)