package com.example.oracleapi.dto.glossary

data class ExportMarkdownResponse(
    val content: String,
    val fileName: String,
    val totalTerms: Int
)