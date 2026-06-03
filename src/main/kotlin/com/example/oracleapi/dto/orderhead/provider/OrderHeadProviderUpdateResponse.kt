package com.example.oracleapi.dto.orderhead.provider

data class OrderHeadProviderUpdateResponse(
    @field:jakarta.validation.constraints.NotNull(message = "orderhead не может быть null")
    @field:jakarta.validation.constraints.Positive(message = "orderhead должен быть положительным")
    val orderhead: Long,
    val agnListNew: com.example.oracleapi.dto.agn.AgnListResponse? = null,
    val agnListOld: com.example.oracleapi.dto.agn.AgnListResponse? = null,
)