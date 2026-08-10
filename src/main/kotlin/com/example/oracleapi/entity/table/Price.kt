package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.math.BigDecimal

@Entity
@Table(name = "PRICE", schema = Helper.SCHEME)
open class Price {
    @Id
    @Column(name = "RN")
    open var rn: Long? = null

    @Column(name = "NOMEN")
    var nomen: BigDecimal? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "NOMEN", insertable = false, updatable = false)
    open var nomenEntity: Nomnlist? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "PBE", insertable = false, updatable = false)
    open var pbeEntity: Pbe? = null

    @Column(name = "PRICE", precision = 17, scale = 2)
    open var price: BigDecimal? = null

    @Column(name = "PRICETYPE")
    open var pricetype: Long? = null

    @ColumnDefault("null")
    @Column(name = "NOMNCAT")
    open var nomncat: Long? = null

    @Column(name = "NORMTN", precision = 17, scale = 2)
    open var normtn: BigDecimal? = null

    @Column(name = "NORMTRADE", precision = 17, scale = 2)
    open var normtrade: BigDecimal? = null

    @Column(name = "NORMTURNOVER", precision = 17, scale = 2)
    open var normturnover: BigDecimal? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "TYPEPRICEACTION", insertable = false, updatable = false)
    open var typepriceactionEntity: Typeprice? = null

}