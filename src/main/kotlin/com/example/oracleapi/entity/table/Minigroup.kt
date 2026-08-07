package com.example.oracleapi.entity.table

import com.example.oracleapi.entity.table.Nomngroup
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
@Table(name = "MINIGROUP", schema = "QREAL")
open class Minigroup {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "NOMNGROUP", nullable = false)
    open var nomngroup: Nomngroup? = null

    @Size(max = 40)
    @NotNull
    @Column(name = "MINIGROUPCODE", nullable = false, length = 40)
    open var minigroupcode: String? = null

    @Size(max = 80)
    @NotNull
    @Column(name = "MINIGROUPNAME", nullable = false, length = 80)
    open var minigroupname: String? = null

    @Size(max = 80)
    @Column(name = "NOTE", length = 80)
    open var note: String? = null

    @ColumnDefault("0")
    @Column(name = "PRICEMIN", precision = 17, scale = 2)
    open var pricemin: BigDecimal? = null

    @ColumnDefault("0")
    @Column(name = "PRICEMAX", precision = 17, scale = 2)
    open var pricemax: BigDecimal? = null

    @ColumnDefault("0")
    @Column(name = "SKU", precision = 17, scale = 2)
    open var sku: BigDecimal? = null

}