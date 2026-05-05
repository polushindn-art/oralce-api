package com.example.oracleapi.entity

import com.example.oracleapi.Helper
import com.example.oracleapi.entity.Userlist
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.math.BigDecimal

@Entity
@Table(name = "NOMNGROUP", schema = Helper.SCHEME)
open class Nomngroup {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "CRN", nullable = false)
    open var crn: Acatalog? = null

    @Size(max = 20)
    @NotNull
    @Column(name = "GROUPCODE", nullable = false, length = 20)
    open var groupcode: String? = null

    @Size(max = 80)
    @NotNull
    @Column(name = "GROUPNAME", nullable = false, length = 80)
    open var groupname: String? = null

    @Size(max = 80)
    @Column(name = "NOTE", length = 80)
    open var note: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "TMCGROUP")
    open var tmcgroup: Tmcgroup? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "SUPPLIER")
    open var supplier: Userlist? = null

    @Column(name = "KSUPPLIER")
    open var ksupplier: Long? = null

    @ColumnDefault("null")
    @Column(name = "MINTORGNAD", precision = 17, scale = 2)
    open var mintorgnad: BigDecimal? = null

    @ColumnDefault("0")
    @Column(name = "COMMONSOS", precision = 17, scale = 2)
    open var commonsos: BigDecimal? = null

    @ColumnDefault("0")
    @Column(name = "MINSOS", precision = 17, scale = 2)
    open var minsos: BigDecimal? = null

    @ColumnDefault("0")
    @Column(name = "MAXSOS", precision = 17, scale = 2)
    open var maxsos: BigDecimal? = null

    @ColumnDefault("0")
    @Column(name = "BALANSSUPLIER", precision = 17, scale = 2)
    open var balanssuplier: BigDecimal? = null

    @ColumnDefault("0")
    @Column(name = "MAXNORMTURNOVER", precision = 17, scale = 2)
    open var maxnormturnover: BigDecimal? = null

    @ColumnDefault("0")
    @Column(name = "SUMSEZONZAKUP", precision = 17, scale = 2)
    open var sumsezonzakup: BigDecimal? = null

    @Column(name = "CONTROL_CAPACITY")
    open var controlCapacity: Boolean? = null

    @ColumnDefault("0")
    @Column(name = "MEASUREDNOMENS")
    open var measurednomens: Boolean? = null

    @ColumnDefault("NULL")
    @Column(name = "ISFOOD")
    open var isfood: Boolean? = null

    @ColumnDefault("NULL")
    @Column(name = "SALETONULL")
    open var saletonull: Boolean? = null

    @ColumnDefault("NULL")
    @Column(name = "UNCHECKOBOROT")
    open var uncheckoborot: Boolean? = null

}