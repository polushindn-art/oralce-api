package com.example.oracleapi.dto.tsdlist

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Ответ после upsert операции")
data class TsdUpsertResponse(
    @Schema(description = "RN терминала")
    val rn: Long,

    @Schema(description = "Device ID")
    val deviceId: String,

    @Schema(description = "SN терминала (если известен)")
    val sn: String?,

    @Schema(description = "Операция (INSERT или UPDATE)")
    val operation: String,

    @Schema(description = "Была ли создана новая запись")
    val isNew: Boolean,

    @Schema(description = "RFID терминала (при INSERT генерируется случайный)")
    val generatedRfid: String
)
