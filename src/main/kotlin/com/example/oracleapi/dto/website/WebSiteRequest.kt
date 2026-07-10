package com.example.oracleapi.dto.website

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Pattern

data class WebSiteRequest(
    @field:Pattern(
        regexp = "^B[0-9]{8,16}$",
        message = "Код должен начинаться с 'B' и содержать от 8 до 10 цифр"
    )
    @field:Schema(
        description = "Артикул товара",
        example = "B0035721800001000")
    val article: String
)