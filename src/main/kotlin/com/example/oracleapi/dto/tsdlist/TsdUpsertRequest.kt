package com.example.oracleapi.dto.tsdlist

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "Запрос на обновление/создание терминала")
data class TsdUpsertRequest(
    @field:Schema(
        description = "Device ID (уникальный идентификатор устройства)",
        required = true,
        example = "android-12345-abcde"
    )
    val deviceId: String,  // Теперь это обязательное поле - ключ для поиска

    @field:Schema(
        description = "Серийный номер (может быть неизвестен при первом обращении)",
        example = "TSD1234567890"
    )
    val sn: String? = null,  // Сделали опциональным

    @field:Schema(description = "Текущая версия ПО", example = "1.2.3")
    val curversion: String? = null,

    @field:Schema(description = "Программа ТСД", example = "com.example.qshop")
    val tsdprogram: String? = null,

    @field:Schema(description = "IP адрес", example = "192.168.1.100")
    val tsdip: String? = null,

    @field:Schema(description = "Имя ТСД", example = "SCANNER_01")
    val tsdname: String? = null,

    @field:Schema(description = "Версия кода", example = "5")
    val versioncode: Long? = null,

    @field:Schema(description = "PBE (подразделение)", example = "1001")
    val pbe: Long? = null,

    @field:Schema(description = "Примечание", example = "Склад А")
    val note: String? = null
)
