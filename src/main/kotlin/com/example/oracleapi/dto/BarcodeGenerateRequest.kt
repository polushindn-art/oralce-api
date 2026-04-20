package com.example.oracleapi.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "Запрос на генерацию GS1 DataMatrix")
data class BarcodeGenerateRequest(
    @Schema(
        description = "GS1 данные в формате со скобками",
        example = "(01)04607161615393(21)em24bGJTT!V*H(91)EE11",
        required = true
    )
    @NotBlank(message = "Данные не могут быть пустыми")
    @Size(max = 500, message = "Данные не должны превышать 500 символов")
    val data: String,

    @Schema(description = "Ширина изображения в пикселях", example = "400", defaultValue = "300")
    val width: Int = 300,

    @Schema(description = "Высота изображения в пикселях", example = "400", defaultValue = "300")
    val height: Int = 300
)

@Schema(description = "Ответ с Base64 изображением")
data class BarcodeGenerateResponse(
    @Schema(description = "Base64 строка PNG изображения")
    val barcodeBase64: String,

    @Schema(description = "Размер матрицы", example = "24x24")
    val matrixSize: String
)