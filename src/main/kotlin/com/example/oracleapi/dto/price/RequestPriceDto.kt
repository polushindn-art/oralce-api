package com.example.oracleapi.dto.price

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*
import org.jetbrains.annotations.NotNull

data class RequestPriceDto(
    @field:NotNull
    @field:Positive(message = "Должен быть задан")
    @field:Schema(description = "")
    val nomen: Long,
    @field:NotNull
    @field:Positive(message = "Должен быть задан")
    val store: Long,
    @field:Min(0, message = "0 - На кассе, 1 - По карте, 2 - В акции, 3 - Розница")
    @field:Max(3, message = "0 - На кассе, 1 - По карте, 2 - В акции, 3 - Розница")
    val typeOut: Int
)
