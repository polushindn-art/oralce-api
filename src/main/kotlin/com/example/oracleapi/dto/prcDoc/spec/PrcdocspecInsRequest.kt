package com.example.oracleapi.dto.prcDoc.spec

import jakarta.validation.constraints.*
import java.math.BigDecimal

data class PrcdocspecInsRequest(

    // Обязательные (не могут быть null)
    @field:NotNull(message = "prn обязателен")
    @field:Positive(message = "prn должен быть положительным")
    val prn: Long,

    @field:NotNull(message = "nomen обязателен")
    @field:Positive(message = "nomen должен быть положительным")
    val nomen: Long,

    @field:NotNull(message = "pricecs обязателен")
    @field:DecimalMin(value = "0.0", inclusive = true, message = "pricecs не может быть отрицательным")
    val pricecs: BigDecimal,

    @field:NotNull(message = "price1 обязателен")
    @field:DecimalMin(value = "0.0", inclusive = true, message = "price1 не может быть отрицательным")
    val price1: BigDecimal,

    @field:NotNull(message = "price2 обязателен")
    @field:DecimalMin(value = "0.0", inclusive = true, message = "price2 не может быть отрицательным")
    val price2: BigDecimal,

    @field:NotNull(message = "price3 обязателен")
    @field:DecimalMin(value = "0.0", inclusive = true, message = "price3 не может быть отрицательным")
    val price3: BigDecimal,

    @field:NotNull(message = "price4 обязателен")
    @field:DecimalMin(value = "0.0", inclusive = true, message = "price4 не может быть отрицательным")
    val price4: BigDecimal,

    @field:NotNull(message = "price5 обязателен")
    @field:DecimalMin(value = "0.0", inclusive = true, message = "price5 не может быть отрицательным")
    val price5: BigDecimal,

    // Параметры со значениями по умолчанию – могут отсутствовать, но если переданы, должны быть положительными или валидными
    @field:PositiveOrZero(message = "enabled должен быть >= 0")
    val enabled: Long = 1,

    @field:PositiveOrZero(message = "overhaul должен быть >= 0")
    val overhaul: Long = 0,

    @field:PositiveOrZero(message = "kopeck должен быть >= 0")
    val kopeck: Long = 0,

    @field:PositiveOrZero(message = "whsconst должен быть >= 0")
    val whsconst: Long = 1,

    @field:Pattern(regexp = "^\\d{6}$", message = "должен содержать 6 цифр")
    val checkRoznPrice: String = "111111",

    // Опциональные поля (могут быть null)
    @field:Positive(message = "nomncat1 должен быть положительным")
    val nomncat1: Long? = null,

    @field:Positive(message = "nomncat2 должен быть положительным")
    val nomncat2: Long? = null,

    @field:Positive(message = "nomncat3 должен быть положительным")
    val nomncat3: Long? = null,

    @field:Positive(message = "nomncat4 должен быть положительным")
    val nomncat4: Long? = null,

    @field:Positive(message = "nomncat5 должен быть положительным")
    val nomncat5: Long? = null,

    @field:Positive(message = "nomncatCS должен быть положительным")
    val nomncatCS: Long? = null,

    @field:DecimalMin(value = "0.0", inclusive = true, message = "overhaulpricepr не может быть отрицательным")
    val overhaulpricepr: BigDecimal? = null,

    @field:Positive(message = "typepriceactioncs должен быть положительным")
    val typepriceactioncs: Long? = null,

    @field:Positive(message = "typepriceaction1 должен быть положительным")
    val typepriceaction1: Long? = null,

    @field:Positive(message = "typepriceaction2 должен быть положительным")
    val typepriceaction2: Long? = null,

    @field:Positive(message = "typepriceaction3 должен быть положительным")
    val typepriceaction3: Long? = null,

    @field:Positive(message = "typepriceaction4 должен быть положительным")
    val typepriceaction4: Long? = null,

    @field:Positive(message = "typepriceaction5 должен быть положительным")
    val typepriceaction5: Long? = null,

    // IN OUT rn – опционально, но если передано, должно быть положительным
    @field:Positive(message = "rn должен быть положительным")
    val rn: Long? = null,

    // Флаги
    val isUpdate: Boolean = false,
    val isWS: Boolean = true
)
