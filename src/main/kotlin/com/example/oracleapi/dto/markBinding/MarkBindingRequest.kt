package com.example.oracleapi.dto.markBinding

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class MarkBindingRequest(

    @field:NotNull(message = "не может быть null")
    @field:Positive(message = "должен быть больше 0")
    val prn: Long,              // код маркировки (MARK.RN)

    @field:NotNull(message = "не может быть null")
    @field:Positive(message = "должен быть больше 0")
    val docRn: Long,            // строка документа

    @field:NotNull(message = "не может быть null")
    @field:NotBlank(message = "не может быть пустым")
    val docTableName: String    // таблица спецификации (WAYSPEC, ORDERSPEC, IDSPEC, TOSPEC, OSSPEC)
)
