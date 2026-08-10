package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "TMCGROUP", schema = Helper.Companion.SCHEME)
open class Tmcgroup {
    @Id
    @Column(name = "RN", columnDefinition = "unknown")
    open var rn: Any? = null

    @Column(name = "TMCGROUPCODE", columnDefinition = "unknown")
    open var tmcgroupcode: Any? = null

    @Column(name = "TMCGROUPNAME", columnDefinition = "unknown")
    open var tmcgroupname: Any? = null

    @Column(name = "BASEGROUP", columnDefinition = "unknown")
    open var basegroup: Any? = null

    @Column(name = "NOTE", columnDefinition = "unknown")
    open var note: Any? = null

    @Column(name = "USEINBUDGET", columnDefinition = "unknown")
    open var useinbudget: Any? = null

}