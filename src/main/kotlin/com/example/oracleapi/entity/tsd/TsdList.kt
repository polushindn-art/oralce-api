package com.example.oracleapi.handler.com.example.oracleapi.entity.tsd

import com.example.oracleapi.Helper
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "tsdlist", schema = Helper.SCHEME)
data class TsdList(
    @Id
    @Column(name = "rn")
    val rn: Long,

    @Column(name = "sn")
    val sn: String
)
