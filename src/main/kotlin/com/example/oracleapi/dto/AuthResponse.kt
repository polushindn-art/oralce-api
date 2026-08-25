package com.example.oracleapi.dto

import com.example.oracleapi.dto.auth.DeviceInfo
import com.example.oracleapi.dto.store.StoreSimpleResponse
import com.example.oracleapi.dto.userpart.PartInfo

data class AuthResponse(
    // Информация об устройстве
    val device: DeviceInfo,

    // Информация о пользователе
    val user: UserAuthInfo,

    // Информация о терминале
    val terminal: TerminalAuthInfo? = null,

    // Токен
    val token: String,
    val expiresIn: Long
)


data class UserAuthInfo(
    val rn: Long,
    val usercode: String,
    val username: String,
    val parole: String,
    val userAgn: Long,
    val dscbarnumb: String,
    val roles: List<PartInfo>  // ← теперь понятно, что это роли
)

data class TerminalAuthInfo(
    val timestart: String?,
    val pbecode: String?,
    val pbern: Long?,
    val stores: List<StoreSimpleResponse>,
    val params: List<ParamBriefInfo>
)

data class ParamBriefInfo(
    val name: String?,
    val value: String?,
    val description: String?
)