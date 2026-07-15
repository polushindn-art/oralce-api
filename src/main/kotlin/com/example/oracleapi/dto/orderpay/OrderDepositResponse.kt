package com.example.oracleapi.dto.orderpay

import java.math.BigDecimal

/**
 * DTO для ответа функции get_order_deposit
 * Распределение вносимых средств по группам номенклатуры заказа
 */
data class OrderDepositResponse(
    val ohrn: Long,                          // ID заказа
    val provider: Long,                       // Поставщик (AGNLIST)
    val nomengroup: Long,                     // ID номенклатурной группы
    val groupcode: String,                    // Код номенклатурной группы
    val sumprprice: BigDecimal,               // Общая сумма заказа
    val prihordSumm: BigDecimal,              // Сумма приходных документов по заказу
    val sumspec: BigDecimal,                  // Сумма по группе
    val persent: BigDecimal,                  // Доля группы в заказе (%)
    val paidFor: BigDecimal,                  // Общая сумма оплат по заказу
    val remaining: BigDecimal,                // Остаток к доплате по заказу
    val remainingGroup: BigDecimal,           // Остаток к доплате по группе
    val overpayment: BigDecimal,              // Переплата по заказу
    val overpaymentGroup: BigDecimal,         // Переплата по группе
    val numContractGroup: String?,            // Номер договора для группы (NULL если нет)
    val contractrn: Long?,                    // ID договора (NULL если нет)
    val orderpay: BigDecimal?,                // Сумма платежа по договору (NULL если нет)
    val depositGroup: BigDecimal              // Сумма вносимых средств на группу
)
