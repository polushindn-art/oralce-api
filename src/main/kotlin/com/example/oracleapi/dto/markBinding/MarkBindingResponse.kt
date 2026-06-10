package com.example.oracleapi.dto.markBinding

import com.example.oracleapi.annotation.BindingDateTimeFormat
import java.time.LocalDateTime

data class MarkBindingResponse(
    val rn: Long,               // RN созданной записи
    val prn: Long,              // код маркировки
    val docRn: Long,            // строка документа
    val docTableName: String,   // таблица спецификации
    @BindingDateTimeFormat
    val bindingDate: LocalDateTime  // дата привязки
)
