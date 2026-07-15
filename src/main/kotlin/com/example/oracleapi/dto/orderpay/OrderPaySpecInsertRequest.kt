package com.example.oracleapi.dto.orderpay
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import org.checkerframework.checker.units.qual.degrees
import java.math.BigDecimal

/**
 * DTO для процедуры PKG_ORDERPAYSPEC.INS
 * Создание спецификации документа оплаты поставщику
 */
data class OrderPaySpecInsertRequest(
    @field:NotNull(message = "ID документа оплаты обязателен")
    @field:Positive(message = "ID документа оплаты должен быть положительным числом")
    @field:JsonProperty("prn_")
    @field:Schema(description = "Заголовок", required = true, example = "207500311640")
    val prn: Long,

    @field:NotNull(message = "ID номенклатурной группы обязателен")
    @field:Positive(message = "ID номенклатурной группы должен быть положительным числом")
    @field:JsonProperty("nomngroup_")
    @field:Schema(description = "Группа", required = true, example = "19371406")
    val nomngroup: Long,

    @field:NotNull(message = "Сумма обязательна")
    @field:Positive(message = "Сумма должна быть положительным числом")
    @field:JsonProperty("summ_")
    @field:Schema(description = "Сумма", required = true, example = "1000.50")
    val summ: BigDecimal
)

/**
 * Ответ на создание спецификации документа оплаты
 */
data class OrderPaySpecInsertResponse(
    @field:JsonProperty("rn_")
    val rn: Long  // ID созданной спецификации
)