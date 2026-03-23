package com.example.oracleapi.dto.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "V_USER")
data class UserList(
    @Id
    @Column(name = "RN")
    val rn: Long,
    @Column(name = "USERCODE")
    val usercode: String,
    @Column(name = "PAROLE")
    val parole: String
)