package com.example.oracleapi.entity.pbe

import com.example.oracleapi.Helper
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "pbe", schema = Helper.SCHEME)
data class PBE(
    @Id
    @Column(name = "rn")
    val rn: Long,
    @Column(name = "pbecode")
    val pbecode: String
)
