package com.example.oracleapi.dto.glossary

import java.time.LocalDateTime

data class GlossaryHistoryResponse(
    val rn: Long,
    val termRn: Long,
    val term: String,
    val definition: String,
    val version: Int,
    val changedBy: String? = null,
    val changedAt: LocalDateTime? = null
)
