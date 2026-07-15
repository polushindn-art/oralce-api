package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import com.example.oracleapi.entity.Measure
import com.example.oracleapi.entity.Nomnlist
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
@Table(name = "MEASUNIT", schema = Helper.Companion.SCHEME)
open class Measunit {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "NOMEN", nullable = false)
    open var nomen: Nomnlist? = null

    @Column(name = "QUANTINSPACK", precision = 17, scale = 7)
    open var quantinspack: BigDecimal? = null

    @NotNull
    @Column(name = "PACKQUANTBASIC", nullable = false, precision = 17, scale = 7)
    open var packquantbasic: BigDecimal? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "MEASURE", nullable = false)
    open var measure: Measure? = null

    @Column(name = "LENGTH", precision = 17, scale = 2)
    open var length: BigDecimal? = null

    @Column(name = "WIDTH", precision = 17, scale = 2)
    open var width: BigDecimal? = null

    @Column(name = "HEIGHT", precision = 17, scale = 2)
    open var height: BigDecimal? = null

    @Column(name = "WEIGHT", precision = 17, scale = 3)
    open var weight: BigDecimal? = null

    @ColumnDefault("0")
    @Column(name = "ISUPMG")
    open var isupmg: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "ISSEH", nullable = false)
    open var isseh: Long? = null

    @ColumnDefault("0")
    @Column(name = "ISUPTP")
    open var isuptp: Long? = null

    @ColumnDefault("0")
    @Column(name = "ISNET")
    open var isnet: Long? = null

    @ColumnDefault("0")
    @Column(name = "ISSERG")
    open var isserg: Long? = null

    @Column(name = "MEASINSPACK")
    open var measinspack: Long? = null

    @Column(name = "PACKAGING")
    open var packaging: Long? = null

    @Size(max = 40)
    @Column(name = "PACKCODE", length = 40)
    open var packcode: String? = null

    @Column(name = "ISADDITIONAL_MEASURE")
    open var isadditionalMeasure: Boolean? = null

    @Column(name = "ISBULKPACKAGE")
    open var isbulkpackage: Boolean? = null

    @Size(max = 320)
    @Column(name = "RULECREATION", length = 320)
    open var rulecreation: String? = null

    @Size(max = 320)
    @Column(name = "RULEDISBANDMENT", length = 320)
    open var ruledisbandment: String? = null

    @Size(max = 320)
    @Column(name = "NOTE", length = 320)
    open var note: String? = null

    @Column(name = "UPINPLACE")
    open var upinplace: Long? = null

    @ColumnDefault("1")
    @Column(name = "ISACTIVE")
    open var isactive: Boolean? = null

    @Size(max = 40)
    @Column(name = "BARCODE", length = 40)
    open var barcode: String? = null

    @ColumnDefault("0")
    @Column(name = "ISGOST")
    open var isgost: Long? = null

    @Column(name = "ISFORREPORT")
    open var isforreport: Boolean? = null

    @ColumnDefault("0")
    @Column(name = "CHECKED")
    open var checked: Boolean? = null

    @ColumnDefault("NULL")
    @Column(name = "ISFORTRADING")
    open var isfortrading: Boolean? = null

    @ColumnDefault("NULL")
    @Column(name = "ISFORSITE")
    open var isforsite: Boolean? = null

}