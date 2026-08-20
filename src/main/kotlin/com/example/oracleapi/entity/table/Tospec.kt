package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.math.BigDecimal

@Entity
@Table(name = "TOSPEC", schema = Helper.SCHEME)
open class Tospec {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @NotNull
    @Column(name = "PRN", nullable = false)
    open var prn: Long? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "NOMEN", nullable = false, insertable = false, updatable = false)
    open var nomenEntity: Nomnlist? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "TDISKONT", nullable = false)
    open var tdiskont: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "DISKONT", nullable = false, precision = 17, scale = 5)
    open var diskont: BigDecimal? = null

    @NotNull
    @Column(name = "PRICE", nullable = false, precision = 17, scale = 2)
    open var price: BigDecimal? = null

    @NotNull
    @Column(name = "PRICEOUT", nullable = false, precision = 17, scale = 2)
    open var priceout: BigDecimal? = null

    @NotNull
    @Column(name = "QUANT", nullable = false, precision = 17, scale = 3)
    open var quant: BigDecimal? = null

    @NotNull
    @Column(name = "SUMM", nullable = false, precision = 17, scale = 2)
    open var summ: BigDecimal? = null

    @JoinColumn(name = "MEASALT", insertable = false, updatable = false)
    open var measalt: Long? = null

    @Column(name = "QUANTALT", precision = 17, scale = 3)
    open var quantalt: BigDecimal? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "STORE", nullable = false, insertable = false, updatable = false)
    open var storeEntity: Store? = null

    @Column(name = "USED_DISKONT")
    open var usedDiskont: Long? = null

    @Column(name = "NOMNCAT")
    open var nomncat: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "OVERHAUL", nullable = false)
    open var overhaul: Long? = null

    @NotNull
    @Column(name = "PRICEAUTO", nullable = false, precision = 17, scale = 2)
    open var priceauto: BigDecimal? = null

    @NotNull
    @ColumnDefault("1")
    @Column(name = "MODIFIED", nullable = false)
    open var modified: Long? = null

    @Column(name = "APSPEC")
    open var apspec: Long? = null

    @NotNull
    @Column(name = "BASEPRICEOUT", nullable = false, precision = 17, scale = 2)
    open var basepriceout: BigDecimal? = null

    @NotNull
    @Column(name = "BASESUM", nullable = false, precision = 17, scale = 2)
    open var basesum: BigDecimal? = null

    @NotNull
    @Column(name = "NDSSUM", nullable = false, precision = 17, scale = 2)
    open var ndssum: BigDecimal? = null

    @NotNull
    @Column(name = "SUMSPEC", nullable = false, precision = 17, scale = 2)
    open var sumspec: BigDecimal? = null

    @NotNull
    @Column(name = "BONUSSUM", nullable = false, precision = 17, scale = 2)
    open var bonussum: BigDecimal? = null

    @ColumnDefault("null")
    @Column(name = "PARTIONS")
    open var partions: Long? = null

    @Column(name = "WEIGHT", precision = 17, scale = 3)
    open var weight: BigDecimal? = null

    @Column(name = "VOLUME", precision = 17, scale = 3)
    open var volume: BigDecimal? = null

    @Column(name = "NDSRATE", precision = 17, scale = 2)
    open var ndsrate: BigDecimal? = null

}