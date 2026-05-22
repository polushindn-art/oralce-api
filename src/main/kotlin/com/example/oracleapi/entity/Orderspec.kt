package com.example.oracleapi.entity

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
import java.time.LocalDate

@Entity
@Table(name = "ORDERSPEC", schema = Helper.SCHEME)
open class Orderspec {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "PRN", nullable = false)
    open var prn: Orderhead? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "NOMEN", nullable = false)
    open var nomen: Nomnlist? = null

    @NotNull
    @Column(name = "QUANT", nullable = false, precision = 17, scale = 3)
    open var quant: BigDecimal? = null

    @NotNull
    @ColumnDefault("1")
    @Column(name = "MODIFIED", nullable = false)
    open var modified: Long? = null

    @NotNull
    @Column(name = "FACTQUANT", nullable = false, precision = 17, scale = 3)
    open var factquant: BigDecimal? = null

    @NotNull
    @Column(name = "PRQUANT", nullable = false, precision = 17, scale = 3)
    open var prquant: BigDecimal? = null

    @NotNull
    @Column(name = "PRSUM", nullable = false, precision = 17, scale = 2)
    open var prsum: BigDecimal? = null

    @Size(max = 40)
    @Column(name = "GTD", length = 40)
    open var gtd: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "COUNTRY")
    open var country: Country? = null

    @NotNull
    @Column(name = "QUANTBREAK", nullable = false, precision = 17, scale = 3)
    open var quantbreak: BigDecimal? = null

    @NotNull
    @Column(name = "SUMM", nullable = false, precision = 17, scale = 2)
    open var summ: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "NDSRATE", nullable = false, precision = 17, scale = 2)
    open var ndsrate: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "PDPRICECS", nullable = false, precision = 17, scale = 2)
    open var pdpricecs: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "PDPRICE1", nullable = false, precision = 17, scale = 2)
    open var pdprice1: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "PDPRICE2", nullable = false, precision = 17, scale = 2)
    open var pdprice2: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "PDPRICE3", nullable = false, precision = 17, scale = 2)
    open var pdprice3: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "PDPRICE4", nullable = false, precision = 17, scale = 2)
    open var pdprice4: BigDecimal? = null

    @NotNull
    @ColumnDefault("1")
    @Column(name = "WHSCONST", nullable = false)
    open var whsconst: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "PDPRICE5", nullable = false, precision = 17, scale = 2)
    open var pdprice5: BigDecimal? = null

    @Size(max = 6)
    @NotNull
    @ColumnDefault("111111")
    @Column(name = "CHECK_ROZN_PRICE", nullable = false, length = 6)
    open var checkRoznPrice: String? = null

    @Column(name = "DIFFERENCE")
    open var difference: Long? = null

    @ColumnDefault("null")
    @Column(name = "PDNOMNCATCS")
    open var pdnomncatcs: Long? = null

    @ColumnDefault("null")
    @Column(name = "PDNOMNCAT1")
    open var pdnomncat1: Long? = null

    @ColumnDefault("null")
    @Column(name = "PDNOMNCAT2")
    open var pdnomncat2: Long? = null

    @ColumnDefault("null")
    @Column(name = "PDNOMNCAT3")
    open var pdnomncat3: Long? = null

    @ColumnDefault("null")
    @Column(name = "PDNOMNCAT4")
    open var pdnomncat4: Long? = null

    @ColumnDefault("null")
    @Column(name = "PDNOMNCAT5")
    open var pdnomncat5: Long? = null

    @Size(max = 120)
    @Column(name = "NOTELOGIST", length = 120)
    open var notelogist: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @ColumnDefault("null")
    @JoinColumn(name = "STOREIN")
    open var storein: Store? = null

    @ColumnDefault("0")
    @Column(name = "AUTOZQUANT", precision = 17, scale = 3)
    open var autozquant: BigDecimal? = null

    @ColumnDefault("0")
    @Column(name = "SRBQUANT", precision = 17, scale = 3)
    open var srbquant: BigDecimal? = null

    @ColumnDefault("0")
    @Column(name = "NOTCONQUANT", precision = 17, scale = 3)
    open var notconquant: BigDecimal? = null

    @ColumnDefault("0")
    @Column(name = "UNDEFINEDQUANT", precision = 17, scale = 3)
    open var undefinedquant: BigDecimal? = null

    @Size(max = 300)
    @ColumnDefault("null")
    @Column(name = "NOTEBREAK", length = 300)
    open var notebreak: String? = null

    @ColumnDefault("0")
    @Column(name = "DLYAKOMPL")
    open var dlyakompl: Long? = null

    @ColumnDefault("0")
    @Column(name = "KOMPLRN")
    open var komplrn: Long? = null

    @ColumnDefault("0")
    @Column(name = "KOMPLQTY")
    open var komplqty: Long? = null

    @ColumnDefault("0")
    @Column(name = "QTYVKOMPL", precision = 17, scale = 3)
    open var qtyvkompl: BigDecimal? = null

    @ColumnDefault("0")
    @Column(name = "CALCQTYPOST", precision = 17, scale = 3)
    open var calcqtypost: BigDecimal? = null

    @ColumnDefault("0")
    @Column(name = "DOCQTYPOST", precision = 17, scale = 3)
    open var docqtypost: BigDecimal? = null

    @ColumnDefault("NULL")
    @Column(name = "RNDEI")
    open var rndei: Long? = null

    @ColumnDefault("0")
    @Column(name = "FACTQTYPOST", precision = 17, scale = 3)
    open var factqtypost: BigDecimal? = null

    @ColumnDefault("NULL")
    @Column(name = "DATE_PRODUCTION")
    open var dateProduction: LocalDate? = null

    @ColumnDefault("0")
    @Column(name = "QUANTDOC", precision = 17, scale = 3)
    open var quantdoc: BigDecimal? = null

    @ColumnDefault("0")
    @Column(name = "SUMMDOC", precision = 17, scale = 10)
    open var summdoc: BigDecimal? = null

}