package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import com.example.oracleapi.entity.table.Userlist
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "TSDLISTHISTORY", schema = Helper.Companion.SCHEME)
open class TsdListHistory {
    @Id
    @Column(name = "RN")
    open var rn: Long? = null

    @Column(name = "TSDLIST", insertable = false, updatable = false)
    open var tsdlist: Long? = null

    @Column(name = "USERLIST", insertable = false, updatable = false)
    open var userlist: Long? = null

    @Column(name = "TIMESTART")
    open var timestart: LocalDateTime? = null

    @Column(name = "TIMEEND")
    open var timeend: LocalDateTime? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TSDLIST")
    open var tsdList: Tsdlist? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERLIST")
    open var userList: Userlist? = null
}