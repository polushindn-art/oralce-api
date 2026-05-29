package com.example.oracleapi.dto.orderhead

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDate

@Schema(description = "Запрос на обновление заказа")
data class OrderHeadUpdRequest(
    @Schema(description = "RN записи (обязателен для обновления)", required = true, example = "123456")
    val rn: Long? = null,

    @Schema(description = "Идентификатор каталога", example = "18230596520")
    val crn: Long? = null,

    @Schema(description = "Тип документа", example = "12451594")
    val doctype: Long? = null,

    @Schema(description = "Префикс документа", example = "ЗАКАЗ_26")
    val docpref: String? = null,

    @Schema(description = "Номер документа")
    val docnumb: BigDecimal? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    @Schema(description = "Дата документа", example = "20.05.2026")
    val docdate: LocalDate? = null,

    @Schema(description = "Склад получения", example = "47677")
    val storein: Long? = null,

    @Schema(description = "Поставщик", example = "103473803540")
    val provider: Long? = null,

    @Schema(description = "Юридическое лицо", example = "52357205100")
    val ul: Long? = null,

    @Schema(description = "Накладные расходы", example = "0.05")
    val overhead: BigDecimal? = null,

    @Schema(description = "Примечание", example = "Обновленный заказ")
    val note: String? = null,

    @Schema(description = "Тип документа основания")
    val basisdoctype: Long? = null,

    @Schema(description = "Префикс документа основания")
    val basisdocpref: String? = null,

    @Schema(description = "Номер документа основания")
    val basisdocnumb: BigDecimal? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    @Schema(description = "Дата документа основания")
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    @Schema(description = "Дата прибытия", example = "25.05.2026")
    val arrivaldate: LocalDate? = null,

    @Schema(description = "Ворота склада", example = "5")
    val storegate: Long? = null,

    @Schema(description = "Коэффициент наценки", example = "10")
    val naclRash: Long? = null,

    @Schema(description = "Максимальный процент", example = "99.9")
    val maxPcent: Double? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    @Schema(description = "Планируемая дата прихода", example = "30.05.2026")
    val planArrivalDate: LocalDate? = null,

    @Schema(description = "Тип товара", example = "ELECTRONICS")
    val nomenType: String? = null,

    @Schema(description = "Тип упаковки", example = "BOX")
    val packType: String? = null
)