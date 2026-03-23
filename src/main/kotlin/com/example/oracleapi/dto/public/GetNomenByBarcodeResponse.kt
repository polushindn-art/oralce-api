package com.example.oracleapi.dto.public

class GetNomenByBarcodeResponse(
    val nomen: Long,                     // Идентификатор, возвращаемый функцией
    val barcode: String,               // Исходный штрих-код
    val executionTimeMs: Long,
    val timestamp: String
)