package com.example.oracleapi.dto.orderhead

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDate

@Schema(description = "Запрос на создание заказа")
data class OrderHeadInsRequest(
    @Schema(description = "Идентификатор каталога", required = true)
    val crn: Long,

    @Schema(description = "Тип документа", required = true)
    val doctype: Long,

    @Schema(description = "Префикс документа", required = true)
    val docpref: String,

    @Schema(description = "Номер документа (опционально, если null - генерируется)")
    val docnumb: Long? = null,

    @Schema(description = "Дата документа", required = true)
    val docdate: LocalDate? = null,

    @Schema(description = "Склад получения", required = true, example = "47677")
    val storein: Long,

    @Schema(description = "Поставщик", required = true)
    val provider: Long,

    @Schema(description = "Юридическое лицо", required = true)
    val ul: Long,

    @Schema(description = "Накладные расходы")
    val overhead: BigDecimal? = null,

    @Schema(description = "Примечание")
    val note: String? = null,

    @Schema(description = "Тип документа основания")
    val basisdoctype: Long? = null,

    @Schema(description = "Префикс документа основания")
    val basisdocpref: String? = null,

    @Schema(description = "Номер документа основания")
    val basisdocnumb: BigDecimal? = null,

    @Schema(description = "Дата документа основания")
    val basisdocdate: LocalDate? = null,

    @Schema(description = "Количество вагонов")
    val numbttn: Long,

    @Schema(description = "Тип ТТН")
    val ttip: Long? = null,

    @Schema(description = "Номер вагона")
    val nvagon: String? = null,

    @Schema(description = "Тип операции")
    val toperation: Long? = null,

    @Schema(description = "Логистическое примечание")
    val notelogist: String? = null,

    @Schema(description = "Специальная отметка")
    val specialmark: Long? = null,

    @Schema(description = "Дата прибытия")
    val arrivaldate: LocalDate? = null,

    @Schema(description = "Ворота склада")
    val storegate: Long? = null,

    @Schema(description = "Коэффициент наценки")
    val naclRash: Long? = null,

    @Schema(description = "Максимальный процент")
    val maxPcent: Double? = null,

    @Schema(description = "RN")
    val rn: Long? = null,

    @Schema(description = "Планируемая дата прихода")
    val planArrivalDate: LocalDate? = null,

    @Schema(description = "Тип товара")
    val nomenType: String? = null,

    @Schema(description = "Тип упаковки")
    val packType: String? = null,

    @Schema(description = "Обновить существующий (по умолчанию false)")
    val isUpdate: Boolean = false
)