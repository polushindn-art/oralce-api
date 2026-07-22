package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import jakarta.persistence.*

@Entity
@Table(name = "MAX_USERS", schema = Helper.SCHEME)
data class MaxUser(
    @Id
    @Column(name = "RN")
    val rn: Long,

    @Column(name = "INTERNAL_NUMBER", nullable = false, unique = true, length = 20)
    val internalNumber: String,

    @Column(name = "USER_ID", nullable = false, length = 50)
    var userId: String,

    @Column(name = "USER_NAME", length = 100)
    var userName: String? = null,

    @Column(name = "CHAT_ID", length = 50)
    var chatId: String? = null
)