package com.example.oracleapi.dto.public

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "Запрос на получение номенклатуры по штрих-коду")
class GetNomenByBarcodeRequest(
    @param:Schema(description = "Штрих-код", example = "4601234567890", required = true)
    @field:NotBlank(message = "Штрих-код не может быть пустым")
    @field:Size(max = 50, message = "Штрих-код не может быть длиннее 50 символов")
    val barcode: String
)