package com.example.oracleapi.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*

@Schema(description = "Запрос на генерацию штрихкода")
data class BarcodeGenerateRequest(
    @field:NotBlank(message = "Данные для штрихкода не могут быть пустыми")
    @field:Size(min = 1, max = 2000, message = "Длина данных должна быть от 1 до 2000 символов")
    @Schema(description = "Данные для кодирования (автоопределение формата)",
        example = "00000046185372KY4mjNZAB=U/FkO")
    val data: String,

    @field:Min(value = 100, message = "Минимальная ширина 100 пикселей")
    @field:Max(value = 1000, message = "Максимальная ширина 1000 пикселей")
    @Schema(description = "Ширина изображения в пикселях", defaultValue = "300")
    val width: Int = 300,

    @field:Min(value = 100, message = "Минимальная высота 100 пикселей")
    @field:Max(value = 1000, message = "Максимальная высота 1000 пикселей")
    @Schema(description = "Высота изображения в пикселях", defaultValue = "300")
    val height: Int = 300
)