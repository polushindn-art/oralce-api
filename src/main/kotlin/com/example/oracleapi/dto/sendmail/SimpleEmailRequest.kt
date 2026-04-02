package com.example.oracleapi.dto.sendmail

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "Запрос на отправку простого письма")
data class SimpleEmailRequest(
    @get:Schema(description = "Заголовок письма", example = "Уведомление")
    @get:NotBlank(message = "Заголовок не может быть пустым")
    @get:Size(max = 255)
    val title: String,

    @get:Schema(description = "Тема письма", example = "Ваш заказ оформлен")
    @get:NotBlank(message = "Тема не может быть пустой")
    @get:Size(max = 255)
    val subject: String,

    @get:Schema(
        description = "Получатель. Можно указать email или логин (добавится @sdl-arsenal.ru)",
        example = "polushin"
    )
    @get:NotBlank(message = "Получатель не может быть пустым")
    @get:Size(max = 320)
    val recipients: String,

    @get:Schema(description = "Текст письма", example = "Здравствуйте!\n\nВаш заказ оформлен.")
    @get:NotBlank(message = "Текст письма не может быть пустым")
    val message: String,

    @field:Schema(description = "Идентификатор документа (опционально)", example = "369852147882222", required = false)
    val tableRn: Long? = null,  // nullable, необязательный

    @field:Schema(description = "Имя таблицы (опционально)", example = "ORDERSPEc", required = false)
    @field:Size(max = 64)
    val tableName: String? = null  // nullable, необязательный
)