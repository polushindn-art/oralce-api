package com.example.oracleapi.entity.table

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault

@Entity
@Table(name = "STOREOPER", schema = "QREAL")
open class Storeoper {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @Size(max = 20)
    @NotNull
    @Column(name = "STROPERCODE", nullable = false, length = 20)
    open var stropercode: String? = null

    @Size(max = 80)
    @NotNull
    @Column(name = "STROPERNAME", nullable = false, length = 80)
    open var stropername: String? = null

    @Size(max = 80)
    @Column(name = "NOTE", length = 80)
    open var note: String? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "TYPETRANS", nullable = false)
    open var typetrans: Long? = null

    @ColumnDefault("NULL")
    @Column(name = "ORDERINDEX")
    open var orderindex: Long? = null

    @NotNull
    @ColumnDefault("1")
    @Column(name = "ACCOUNTING", nullable = false)
    open var accounting: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "ISIDHEAD", nullable = false)
    open var isidhead: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "ISTOHEAD", nullable = false)
    open var istohead: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "ISSTOREOUT", nullable = false)
    open var isstoreout: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "ISSTOREIN", nullable = false)
    open var isstorein: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "ISPROVIDER", nullable = false)
    open var isprovider: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "ISUL", nullable = false)
    open var isul: Long? = null

    @Column(name = "OPERTOSALE")
    open var opertosale: Long? = null

    @Column(name = "OPERRETURN")
    open var operreturn: Long? = null

    @Column(name = "OPERBREAK")
    open var operbreak: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "ISPARTHEAD", nullable = false)
    open var isparthead: Long? = null

    @Column(name = "OPERPART")
    open var operpart: Long? = null

    @NotNull
    @ColumnDefault("1")
    @Column(name = "REFLECT", nullable = false)
    open var reflect: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "CONNECTEDDOC", nullable = false)
    open var connecteddoc: Boolean? = null

    @Column(name = "STOREOPERIN")
    open var storeoperin: Long? = null

    @Size(max = 20)
    @Column(name = "STOREOPERINCOL", length = 20)
    open var storeoperincol: String? = null

    @Column(name = "STOREOPEROUT")
    open var storeoperout: Long? = null

    @Size(max = 20)
    @Column(name = "STOREOPEROUTCOL", length = 20)
    open var storeoperoutcol: String? = null

    @Column(name = "EXCLUDENOMENTYPE")
    open var excludenomentype: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "NOMENQUANT", nullable = false)
    open var nomenquant: Long? = null

}