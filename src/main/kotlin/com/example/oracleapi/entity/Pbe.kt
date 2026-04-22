package com.example.oracleapi.entity

import com.example.oracleapi.Helper
import jakarta.persistence.*

@Entity
@Table(name = "pbe", schema = Helper.SCHEME)
data class Pbe(
    @Id
    @Column(name = "rn")
    val rn: Long,
    @Column(name = "pbecode")
    val pbecode: String
)