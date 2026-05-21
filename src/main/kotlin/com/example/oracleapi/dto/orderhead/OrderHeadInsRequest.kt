package com.example.oracleapi.dto.orderhead

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDate

@Schema(description = "Запрос на создание заказа")
data class OrderHeadInsRequest(

    @Schema(description = "Идентификатор каталога", required = true, example = "18230596520")
    val crn: Long? = null,

    @Schema(description = "Тип документа", required = true, example = "12451594")
    val doctype: Long? = null,

    @Schema(description = "Префикс документа", required = true, example = "ЗАКАЗ_26")
    val docpref: String? = null,

    @Schema(description = "Номер документа (опционально, если null - генерируется)", example = "12345")
    val docnumb: BigDecimal? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "Дата документа", required = true, example = "2026-05-20")
    val docdate: LocalDate? = null,

    @Schema(description = "Склад получения", required = true, example = "47677")
    val storein: Long? = null,

    @Schema(description = "Поставщик", required = true, example = "103473803540")
    val provider: Long? = null,

    @Schema(description = "Юридическое лицо", required = true, example = "52357205100")
    val ul: Long? = null,

    @Schema(description = "Накладные расходы", example = "0.05")
    val overhead: BigDecimal? = null,

    @Schema(description = "Примечание", example = "Тестовый заказ")
    val note: String? = null,

    @Schema(description = "Тип документа основания", example = "1")
    val basisdoctype: Long? = null,

    @Schema(description = "Префикс документа основания", example = "INV")
    val basisdocpref: String? = null,

    @Schema(description = "Номер документа основания", example = "100")
    val basisdocnumb: BigDecimal? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "Дата документа основания", example = "2026-05-19")
    val basisdocdate: LocalDate? = null,

    @Schema(description = "Количество вагонов", example = "2")
    val numbttn: Long? = null,

    @Schema(description = "Тип ТТН", example = "1")
    val ttip: Long? = null,

    @Schema(description = "Номер вагона", example = "12345678")
    val nvagon: String? = null,

    @Schema(description = "Тип операции", example = "1")
    val toperation: Long? = null,

    @Schema(description = "Логистическое примечание", example = "Срочная доставка")
    val notelogist: String? = null,

    @Schema(description = "Специальная отметка", example = "1")
    val specialmark: Long? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "Дата прибытия", example = "2026-05-25")
    val arrivaldate: LocalDate? = null,

    @Schema(description = "Ворота склада", example = "5")
    val storegate: Long? = null,

    @Schema(description = "Коэффициент наценки", example = "10")
    val naclRash: Long? = null,

    @Schema(description = "Максимальный процент", example = "99.9")
    val maxPcent: Double? = null,

    @Schema(description = "RN", example = "123456789")
    val rn: Long? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "Планируемая дата прихода", example = "2026-05-30")
    val planArrivalDate: LocalDate? = null,

    @Schema(description = "Тип товара", example = "ELECTRONICS")
    val nomenType: String? = null,

    @Schema(description = "Тип упаковки", example = "BOX")
    val packType: String? = null,

    @Schema(description = "Обновить существующий (по умолчанию false)", example = "false")
    val isUpdate: Boolean = false
)