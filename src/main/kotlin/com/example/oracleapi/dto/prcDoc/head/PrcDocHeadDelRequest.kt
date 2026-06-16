package com.example.oracleapi.dto.prcDoc.head

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Positive

data class PrcDocHeadDelRequest(
    @field:Positive(message = "должно быть больше 0")
    @field:Schema(description = "RN спецификации", example = "0")
    val rn: Long,

    @field:Schema(example = "false")
    val isUpdate: Boolean = false
)