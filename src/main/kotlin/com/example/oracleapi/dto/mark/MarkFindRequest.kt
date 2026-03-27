package com.example.oracleapi.dto.mark

import jakarta.validation.constraints.NotBlank

data class MarkFindRequest(
    @field:NotBlank(message = "KM обязателен")
    val km: String
)
