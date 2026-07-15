package com.example.oracleapi.dto.orderpay

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*
import java.math.BigDecimal

/**
 * Запрос на расчет распределения вносимых средств
 * Используется для вызова функции get_order_deposit
 */
data class OrderDepositRequest(
    @field:NotNull(message = "ID заказа обязательно")
    @field:Positive(message = "ID заказа должен быть положительным числом")
    @field:JsonProperty("orderRn")
    @field:Schema(description = "Заказ поставщику", example = "207221736060")
    var orderRn: Long,

    @field:NotNull(message = "Сумма вносимых средств обязательна")
    @field:PositiveOrZero(message = "Сумма вносимых средств должна быть больше или равна 0")
    @field:JsonProperty("deposit")
    @field:Schema(description = "Сумма платежа", example = "1000")
    var deposit: BigDecimal
)
