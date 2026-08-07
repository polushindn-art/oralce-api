package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import com.example.oracleapi.entity.table.Nomnlist
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
@Table(name = "PRCDOCSPEC", schema = Helper.SCHEME)
open class Prcdocspec {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "PRN", nullable = false)
    open var prn: Prcdochead? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "NOMEN", nullable = false)
    open var nomen: Nomnlist? = null

    @NotNull
    @ColumnDefault("1")
    @Column(name = "MODIFIED", nullable = false)
    open var modified: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "OVERHAUL", nullable = false)
    open var overhaul: Long? = null

    @NotNull
    @ColumnDefault("1")
    @Column(name = "ENABLED", nullable = false)
    open var enabled: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "KOPECK", nullable = false)
    open var kopeck: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "PRICECS", nullable = false, precision = 17, scale = 2)
    open var pricecs: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "PRICE1", nullable = false, precision = 17, scale = 2)
    open var price1: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "PRICE2", nullable = false, precision = 17, scale = 2)
    open var price2: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "PRICE3", nullable = false, precision = 17, scale = 2)
    open var price3: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "PRICE4", nullable = false, precision = 17, scale = 2)
    open var price4: BigDecimal? = null

    @NotNull
    @ColumnDefault("1")
    @Column(name = "WHSCONST", nullable = false)
    open var whsconst: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "PRICE5", nullable = false, precision = 17, scale = 2)
    open var price5: BigDecimal? = null

    @Size(max = 6)
    @NotNull
    @ColumnDefault("111111")
    @Column(name = "CHECK_ROZN_PRICE", nullable = false, length = 6)
    open var checkRoznPrice: String? = null

    @ColumnDefault("null")
    @Column(name = "NOMNCAT1")
    open var nomncat1: Long? = null

    @ColumnDefault("null")
    @Column(name = "NOMNCAT2")
    open var nomncat2: Long? = null

    @ColumnDefault("null")
    @Column(name = "NOMNCAT3")
    open var nomncat3: Long? = null

    @ColumnDefault("null")
    @Column(name = "NOMNCAT4")
    open var nomncat4: Long? = null

    @ColumnDefault("null")
    @Column(name = "NOMNCAT5")
    open var nomncat5: Long? = null

    @ColumnDefault("null")
    @Column(name = "NOMNCATCS")
    open var nomncatcs: Long? = null

    @ColumnDefault("0")
    @Column(name = "OVERHAULPRICEPR", precision = 17, scale = 2)
    open var overhaulpricepr: BigDecimal? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "TYPEPRICEACTION1")
    open var typepriceaction1: Typeprice? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "TYPEPRICEACTION2")
    open var typepriceaction2: Typeprice? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "TYPEPRICEACTION3")
    open var typepriceaction3: Typeprice? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "TYPEPRICEACTION4")
    open var typepriceaction4: Typeprice? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "TYPEPRICEACTION5")
    open var typepriceaction5: Typeprice? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "TYPEPRICEACTIONCS")
    open var typepriceactioncs: Typeprice? = null

    @Column(name = "PRCDOCSPEC_STATUS")
    open var prcdocspecStatus: Long? = null

}