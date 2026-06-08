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
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.math.BigDecimal

@Entity
@Table(name = "ORDERNAKLSPEC", schema = Helper.SCHEME)
open class Ordernaklspec {
    @Id
    @Column(name = "RN", nullable = false)
    open var id: Long? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "PRN", nullable = false)
    open var prn: Ordernaklhead? = null

    @NotNull
    @Column(name = "NOMEN", nullable = false)
    open var nomen: Long? = null

    @NotNull
    @ColumnDefault("NULL")
    @Column(name = "QUANT", nullable = false, precision = 17, scale = 3)
    open var quant: BigDecimal? = null

    @Column(name = "MEASALT")
    open var measalt: Long? = null

    @Column(name = "QUANTALT", precision = 17, scale = 3)
    open var quantalt: BigDecimal? = null

    @NotNull
    @Column(name = "SUMM", nullable = false, precision = 17, scale = 2)
    open var summ: BigDecimal? = null

    @NotNull
    @Column(name = "INPRICE", nullable = false, precision = 17, scale = 10)
    open var inprice: BigDecimal? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "COUNTRY", nullable = false)
    open var country: Country? = null

    @Size(max = 40)
    @Column(name = "GTD", length = 40)
    open var gtd: String? = null

    @NotNull
    @ColumnDefault("1")
    @Column(name = "MODIFIED", nullable = false)
    open var modified: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "NDSRATE", nullable = false, precision = 17, scale = 2)
    open var ndsrate: BigDecimal? = null

}