package com.example.oracleapi.entity.table

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "APHEAD", schema = "QREAL")
open class Aphead {
    @Id
    @Column(name = "RN", nullable = false)
    open var id: Long? = null

    @Size(max = 10)
    @NotNull
    @Column(name = "DOCPREF", nullable = false, length = 10)
    open var docpref: String? = null

    @NotNull
    @Column(name = "DOCNUMB", nullable = false, precision = 17, scale = 2)
    open var docnumb: BigDecimal? = null

    @NotNull
    @Column(name = "DOCDATE", nullable = false)
    open var docdate: LocalDate? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "TDISKONT", nullable = false)
    open var tdiskont: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "DISKONT", nullable = false, precision = 17, scale = 5)
    open var diskont: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "SUMPRICE", nullable = false, precision = 17, scale = 2)
    open var sumprice: BigDecimal? = null

    @Size(max = 80)
    @Column(name = "NOTE", length = 80)
    open var note: String? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "SUMDOC", nullable = false, precision = 17, scale = 2)
    open var sumdoc: BigDecimal? = null

    @Column(name = "PAYDATE")
    open var paydate: LocalDate? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "NDSRATE", nullable = false, precision = 17, scale = 2)
    open var ndsrate: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "AP_STATUS", nullable = false)
    open var apStatus: Long? = null

    @NotNull
    @Column(name = "LASTDATE", nullable = false)
    open var lastdate: LocalDate? = null

    @Size(max = 20)
    @Column(name = "PAYMENT", length = 20)
    open var payment: String? = null

    @NotNull
    @ColumnDefault("1")
    @Column(name = "MODIFIED", nullable = false)
    open var modified: Long? = null

    @Size(max = 20)
    @Column(name = "RESPONCODE", length = 20)
    open var responcode: String? = null

    @Column(name = "STATUSDATE3")
    open var statusdate3: LocalDate? = null

    @Column(name = "STATUSDATE4")
    open var statusdate4: LocalDate? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "BASESUM", nullable = false, precision = 17, scale = 2)
    open var basesum: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "NDSSUM", nullable = false, precision = 17, scale = 2)
    open var ndssum: BigDecimal? = null

    @Column(name = "SPECIALMARK")
    open var specialmark: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "TOPERATION", nullable = false)
    open var toperation: Boolean? = null

    @ColumnDefault("null")
    @Column(name = "AGNLIST")
    open var agnlist: Long? = null

    @ColumnDefault("null")
    @Column(name = "WWWHEAD")
    open var wwwhead: Long? = null

    @Column(name = "SUMWEIGHT", precision = 17, scale = 3)
    open var sumweight: BigDecimal? = null

    @Column(name = "SUMVOLUME", precision = 17, scale = 3)
    open var sumvolume: BigDecimal? = null

    @Column(name = "OBJECTS", precision = 17, scale = 2)
    open var objects: BigDecimal? = null

    @Column(name = "CURCLE", precision = 17, scale = 2)
    open var curcle: BigDecimal? = null

    @ColumnDefault("NULL")
    @Column(name = "NEEDNDS")
    open var neednds: Long? = null

    @ColumnDefault("0")
    @Column(name = "PRINTNOTESTATUS")
    open var printnotestatus: Boolean? = null

    @Size(max = 80)
    @Column(name = "CLIENTCOLOR", length = 80)
    open var clientcolor: String? = null

    @ColumnDefault("0")
    @Column(name = "CASHFLAG")
    open var cashflag: Long? = null

}