package com.example.oracleapi.dto.auth

import com.example.oracleapi.dto.userpart.PartInfo

data class UserAuthInfo(
    val rn: Long,
    val usercode: String,
    val username: String,
    val parole: String,
    val userAgn: Long,
    val dscbarnumb: String,
    val roles: List<PartInfo>
)
