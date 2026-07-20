package com.example.oracleapi.dto.mark

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

/**
 * DTO для процедуры PKG_MARK.UPD
 */
data class MarkUpdRequest(
    @field:NotBlank(message = "KM обязателен")
    val km: String,
    val json: List<Map<String, Any>>? = null,
    val table: String? = null,
    val tablern: Long? = null,
    @field:NotNull(message = "Status обязателен")
    val status: Int,
    val note: String? = null
)