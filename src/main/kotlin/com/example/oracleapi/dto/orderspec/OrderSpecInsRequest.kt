package com.example.oracleapi.dto.orderspec

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDate

@Schema(description = "Запрос на создание/обновление спецификации заказа")
data class OrderSpecInsRequest(

    @Schema(description = "RN родительского документа (ORDERHEAD)", required = true, example = "205586021350")
    val prn: Long? = null,

    @Schema(description = "Номенклатура", required = true, example = "758353")
    val nomen: Long? = null,

    @Schema(description = "Количество", required = true, example = "10.5")
    val quant: BigDecimal? = null,

    @Schema(description = "Сумма", required = true, example = "15000.00")
    val summ: BigDecimal? = null,

    @Schema(description = "Фактическое количество", example = "0")
    val factquant: BigDecimal? = null,

    @Schema(description = "Количество разрыва", example = "0")
    val quantbreak: BigDecimal? = null,

    @Schema(description = "Не подтвержденное количество", example = "0")
    val notconquant: BigDecimal? = null,

    @Schema(description = "Неопределенное количество", example = "0")
    val undefinedquant: BigDecimal? = null,

    @Schema(description = "Цена", required = true, example = "1500.00")
    val prquant: BigDecimal? = null,

    @Schema(description = "Сумма по цене", example = "0")
    val prsum: BigDecimal? = null,

    @Schema(description = "Автоматическое количество", example = "0")
    val autozquant: BigDecimal? = null,

    @Schema(description = "Количество SRB", example = "0")
    val srbquant: BigDecimal? = null,

    @Schema(description = "Ставка НДС", example = "20")
    val ndsrate: BigDecimal? = null,

    @Schema(description = "Страна", example = "58408262")
    val country: Long? = null,

    @Schema(description = "ГТД", example = "10123456")
    val gtd: String? = null,

    @Schema(description = "Цена CS", example = "0")
    val pdpricecs: BigDecimal? = null,

    @Schema(description = "Цена 1", example = "0")
    val pdprice1: BigDecimal? = null,

    @Schema(description = "Цена 2", example = "0")
    val pdprice2: BigDecimal? = null,

    @Schema(description = "Цена 3", example = "0")
    val pdprice3: BigDecimal? = null,

    @Schema(description = "Цена 4", example = "0")
    val pdprice4: BigDecimal? = null,

    @Schema(description = "Цена 5", example = "0")
    val pdprice5: BigDecimal? = null,

    @Schema(description = "Номинальный каталог CS", example = "0")
    val pdnomncatcs: Long? = null,

    @Schema(description = "Номинальный каталог 1", example = "0")
    val pdnomncat1: Long? = null,

    @Schema(description = "Номинальный каталог 2", example = "0")
    val pdnomncat2: Long? = null,

    @Schema(description = "Номинальный каталог 3", example = "0")
    val pdnomncat3: Long? = null,

    @Schema(description = "Номинальный каталог 4", example = "0")
    val pdnomncat4: Long? = null,

    @Schema(description = "Номинальный каталог 5", example = "0")
    val pdnomncat5: Long? = null,

    @Schema(description = "Логистическое примечание", example = "")
    val notelogist: String? = null,

    @Schema(description = "Константа склада", example = "1")
    val whsconst: Long? = null,

    @Schema(description = "Проверка розничной цены", example = "111111")
    val checkRoznPrice: String? = null,

    @Schema(description = "Склад получения", example = "485737069")
    val storein: Long? = null,

    @Schema(description = "RN записи (для обновления)", example = "0")
    val rn: Long? = null,

    @Schema(description = "Обновить существующий", example = "false")
    val isUpdate: Boolean = false,

    @Schema(description = "WS флаг", example = "true")
    val isWS: Boolean = true,

    @Schema(description = "Изменение накладных расходов", example = "0")
    val changeOverHead: Long? = null,

    @Schema(description = "Для комплекта", example = "0")
    val dlyaKompl: Long? = null,

    @Schema(description = "RN комплекта", example = "0")
    val komplRn: Long? = null,

    @Schema(description = "Количество в комплекте", example = "0")
    val komplQty: Long? = null,

    @Schema(description = "Количество в комплекте (расчетное)", example = "0")
    val qtyvKompl: BigDecimal? = null,

    @Schema(description = "Расчетное количество поставки", example = "0")
    val calcQtyPost: BigDecimal? = null,

    @Schema(description = "Количество документа поставки", example = "0")
    val docQtyPost: BigDecimal? = null,

    @Schema(description = "RN DEI", example = "0")
    val rnDEI: Long? = null,

    @Schema(description = "Фактическое количество поставки", example = "0")
    val factQtyPost: BigDecimal? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    @Schema(description = "Дата производства", example = "31.12.2026")
    val dateProduction: LocalDate? = null,

    @Schema(description = "Количество в документе", example = "0")
    val quantdoc: BigDecimal? = null,

    @Schema(description = "Сумма в документе", example = "0")
    val summdoc: BigDecimal? = null
)