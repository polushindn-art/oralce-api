package com.example.oracleapi.entity.store

import com.example.oracleapi.Helper
import jakarta.persistence.*

@Entity
@Table(name = "store", schema = Helper.SCHEME)
data class Store(
    @Id
    @Column(name = "rn")
    val rn: Long,

    @Column(name = "STOREPBE", nullable = false)
    val storepbe: Long,

    @Column(name = "STORECODE", nullable = false, unique = true)
    val storecode: String,

    @Column(name = "STORENAME", nullable = false)
    val storename: String,

    @Column(name = "TSD_PBE", nullable = false)
    val tsdPbe: Long
)
