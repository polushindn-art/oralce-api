package com.example.oracleapi.dto.glossary

data class SaveGlossaryRequest(
    val term: String,
    val definition: String,
    val category: String? = null
)
