package com.example.oracleapi.dto.vMark

import com.example.oracleapi.dto.mark.ParseMarkResponse
import com.example.oracleapi.dto.mark.VMarkFindResponse

data class MarkSearchResponse(
    val found: Boolean,
    val parseResult: ParseMarkResponse,
    val mark: VMarkFindResponse?
)
