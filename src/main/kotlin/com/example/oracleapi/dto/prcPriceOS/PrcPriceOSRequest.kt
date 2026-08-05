package com.example.oracleapi.dto.prcPriceOS

import com.example.oracleapi.annotation.BindingDateFormat
import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDate

data class PrcPriceOSRequest(
    @field:Schema(description = "Номенклатура", required = true, example = "41228593070")
    @field:NotNull(message = "Должно быть задана")
    @field:Positive(message = "должно быть больше 0")
    var nomen: Long,

    @field:Schema(description = "Цена", required = true, example = "100")
    @field:NotNull(message = "Должно быть задана")
    @field:Positive(message = "должно быть больше 0")
    var price: BigDecimal,

    @field:Schema(description = "Ссылка на группу скидок ОС", required = true, example = "36309778120")
    @field:NotNull(message = "Должно быть задана")
    @field:Positive(message = "должно быть больше 0")
    var dscgroup: Long,

    @field:Schema(description = "Дата начала действия цены будущего периода", required = true, example = "01.08.2026")
    @field:NotNull(message = "Должно быть задана")
    @field:BindingDateFormat
    var futuredate: LocalDate,

    @field:Schema(description = "Регион", required = true, example = "49385072240")
    @field:NotNull(message = "Должно быть задана")
    @field:Positive(message = "должно быть больше 0")
    var region: Long,

    @field:Schema(description = "Ссылка на локумент изменения ТМЦ", required = true, example = "181876716980")
    @field:NotNull(message = "Должно быть задана")
    @field:Positive(message = "должно быть больше 0")
    var prcdochead: Long,

    @field:Schema(description = "Процент скидки", required = true, example = "10")
    @field:NotNull(message = "Должно быть задана")
    @field:Positive(message = "должно быть больше 0")
    var percent: BigDecimal
)
