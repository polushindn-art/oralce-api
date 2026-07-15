package com.example.oracleapi.dto.orderpay
import com.example.oracleapi.annotation.BindingDateFormat
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.LocalDate

/**
 * DTO для процедуры PKG_ORDERPAYHEAD.INS
 * Создание заголовка документа оплаты поставщику
 */
data class OrderPayHeadInsertRequest(
    @field:NotNull(message = "ID заказа обязательно")
    @field:Positive(message = "ID заказа должен быть положительным числом")
    @field:JsonProperty("orderhead_")
    @field:Schema(description = "Заказ поставщику", required = true, example = "207364225750")
    val orderhead: Long,

    @field:NotNull(message = "Статус обязателен")
    @field:JsonProperty("status_")
    @field:Schema(description = "Статус // 0 - Планируемый, 1 - Оплачен, 2 - Утвержден КД", required = true, example = "0")
    val status: Long,  // 0 - Планируемый, 1 - Оплачен, 2 - Утвержден КД

    @field:BindingDateFormat
    @field:NotNull(message = "Дата документа обязательна")
    @field:JsonProperty("docdate_")
    @field:Schema(description = "Дата документа", required = true, example = "20.05.2026")
    val docdate: LocalDate,

    @field:BindingDateFormat
    @field:JsonProperty("plandate_")
    @field:Schema(description = "Планируемая дата оплаты", required = false, example = "20.05.2026")
    val plandate: LocalDate? = null,

    @field:JsonProperty("note_")
    @field:Size(max = 1000, message = "Примечание не должно превышать 1000 символов")
    @field:Schema(description = "Примечание", required = false)
    val note: String? = null,

    @field:NotNull(message = "Порядок оплаты обязателен")
    @field:JsonProperty("orderpay_")
    @field:Schema(description = "Порядок оплаты // 0 - Не определен, 1 - ПО, 2 - ПР, 3 - Другое", required = true, example = "1")
    val orderpay: Long,  // 0 - Не определен, 1 - ПО, 2 - ПР, 3 - Другое

    @field:Schema(description = "Идентификатор созданного заказа", required = false, example = "207500311640")
    @field:JsonProperty("num_contract_")
    val numContract: Long? = null  // ID договора
)

/**
 * Ответ на создание заголовка документа оплаты
 */
data class OrderPayHeadInsertResponse(
    @field:JsonProperty("rn_")
    @field:Schema(description = "Идентификатор созданного заказа")
    val rn: Long  // ID созданного документа
)