package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import com.example.oracleapi.entity.table.Measunit
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
import java.time.LocalDate

@Entity
@Table(name = "IDSPEC", schema = Helper.SCHEME)
open class Idspec {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "PRN", nullable = false)
    open var prn: Idhead? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "NOMEN", nullable = false)
    open var nomen: Nomnlist? = null

    @NotNull
    @ColumnDefault("NULL")
    @Column(name = "QUANT", nullable = false, precision = 17, scale = 3)
    open var quant: BigDecimal? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "MEASALT")
    open var measalt: Measunit? = null

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

    @ColumnDefault("null")
    @Column(name = "PARTIONS")
    open var partions: Long? = null

    @ColumnDefault("NULL")
    @Column(name = "PLANQUANT", precision = 17, scale = 3)
    open var planquant: BigDecimal? = null

    @Column(name = "STOREIN")
    open var storein: Long? = null

    @Column(name = "STOREOUT")
    open var storeout: Long? = null

    @Column(name = "WEIGHT", precision = 17, scale = 3)
    open var weight: BigDecimal? = null

    @Column(name = "VOLUME", precision = 17, scale = 3)
    open var volume: BigDecimal? = null

    @ColumnDefault("null")
    @Column(name = "SORT_ORDER")
    open var sortOrder: Long? = null

    @Column(name = "ISBREAKNOMEN")
    open var isbreaknomen: Long? = null

    @Column(name = "ISCANMOVEBREAK")
    open var iscanmovebreak: Long? = null

    @Column(name = "COLLQUANT", precision = 17, scale = 3)
    open var collquant: BigDecimal? = null

    @Column(name = "RESEIVEDQUANT", precision = 17, scale = 3)
    open var reseivedquant: BigDecimal? = null

    @Column(name = "SHIPQUANT", precision = 17, scale = 3)
    open var shipquant: BigDecimal? = null

    @Column(name = "DECISION")
    open var decision: Long? = null

    @ColumnDefault("null")
    @Column(name = "USERTSD")
    open var usertsd: Long? = null

    @Column(name = "DATETIMETSD")
    open var datetimetsd: LocalDate? = null

    @Size(max = 300)
    @ColumnDefault("null")
    @Column(name = "NOTEBREAK", length = 300)
    open var notebreak: String? = null

    @ColumnDefault("0")
    @Column(name = "SIGNS")
    open var signs: Long? = null

    @ColumnDefault("0")
    @Column(name = "STATUS")
    open var status: Long? = null

    @ColumnDefault("null")
    @Column(name = "DATE_CREATE")
    open var dateCreate: LocalDate? = null

}