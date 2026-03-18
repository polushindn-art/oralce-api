package com.example.oracleapi.dto.mark

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

/**
 * DTO для процедуры PKG_MARK.UPD
 */
data class MarkUpdRequest(
    @field:NotBlank(message = "KM обязателен")
    val km: String,

    @field:NotNull(message = "JSON обязателен")
    val json: List<Map<String, Any>>,

    @field:NotBlank(message = "Table обязательна")
    val table: String,

    @field:NotNull(message = "Tablern обязателен")
    val tablern: Number,

    @field:NotNull(message = "Status обязателен")
    val status: Int,

    val note: String? = null
)