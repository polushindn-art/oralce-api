package com.example.oracleapi.dto.userpart

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PartInfo(
    val rn: Long,
    val partcode: String,
    val partname: String
)