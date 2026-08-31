package com.example.oracleapi.dto.glossary

import java.time.LocalDateTime

data class GlossaryTermResponse(
    val rn: Long,
    val term: String,
    val definition: String,
    val category: String? = null,
    val sortOrder: Int = 0,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val version: Int = 1
)