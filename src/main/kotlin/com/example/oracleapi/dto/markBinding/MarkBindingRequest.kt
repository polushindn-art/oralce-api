package com.example.oracleapi.dto.markBinding

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*

data class MarkBindingRequest(

    @field:NotBlank(message = "КМ должен быть задан")
    @field:Schema(
        required = true
    )
    val km: String,

    @field:Schema(
        required = true
    )
    @field:Positive(message = "должен быть больше 0")
    val docRn: Long,            // строка документа

    @field:Schema(
        required = true
    )
    @field:Pattern(regexp = "^[A-Z]+$", message = "должен содержать только латинские буквы (A-Z)")
    @field:NotBlank(message = "не может быть пустым")
    val docTableName: String,    // таблица спецификации (WAYSPEC, ORDERSPEC, IDSPEC, TOSPEC, OSSPEC)


    val status: Int = 0,

    val note: String? = null
)
