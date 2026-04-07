package com.example.oracleapi.dto.idhead.prihod

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "Запрос на создание приходного ордера")
data class PrihodRequest(
    @Schema(description = "Заголовок документа")
    @JsonProperty("head")
    val head: List<Head>,

    @Schema(description = "Спецификация товаров")
    @JsonProperty("spec")
    val spec: List<Spec>
) {
    @Schema(description = "Заголовок приходного ордера")
    data class Head(
        @Schema(description = "Склад получатель", example = "47677")
        val storein: Long,

        @Schema(description = "Поставщик", example = "ИП ПОЛУНИН")
        val provider: String,

        @Schema(description = "Юридическое лицо", example = "Арсенал-1")
        val ul: String,

        @Schema(description = "Пользователь", example = "90848800390")
        val user: Long,

        @Schema(description = "Примечание", example = "Пользователь загружен из Oracle")
        val note: String?,

        @Schema(description = "Тип документа основания", example = "УПД")
        val basisdoctype: String,

        @Schema(description = "Префикс документа основания", example = "1C")
        val basisdocpref: String,

        @Schema(description = "Номер документа основания", example = "1")
        val basisdocnumb: String,

        @Schema(description = "Дата документа основания", example = "10.10.2026")
        @JsonFormat(pattern = "dd.MM.yyyy")
        val basisdocdate: LocalDate? = null,

        @Schema(description = "Номер ТТН", example = "СФ00-000046")
        val numbttn: String?
    )

    @Schema(description = "Спецификация товаров")
    data class Spec(
        @Schema(description = "Номенклатура RN", example = "121802695400")
        val nomen: Long,

        @Schema(description = "Количество приходуемое", example = "6")
        val quant_upd: Int,

        @Schema(description = "Цена", example = "39.86")
        val inprice: Double,

        @Schema(description = "Количество заказанное", example = "6")
        val quant_order: Int,

        @Schema(description = "Ставка НДС", example = "22")
        val nds_rait: Int,

        @Schema(description = "Страна", example = "РОССИЯ")
        val country: String,

        @Schema(description = "ГТД", example = "10702070/230125/5030388")
        val gtd: String?,

        @Schema(description = "Склад", example = "47677")
        val storein: Long
    )
}