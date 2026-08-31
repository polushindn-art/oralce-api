package com.example.oracleapi.dto.user

import jakarta.persistence.*

@Entity
@Table(name = "V_USER")
data class VUserListResponse(
    @Id
    @Column(name = "RN")
    val rn: Long,
    @Column(name = "USERCODE")
    val usercode: String,
    @Column(name = "PAROLE")
    val parole: String
)