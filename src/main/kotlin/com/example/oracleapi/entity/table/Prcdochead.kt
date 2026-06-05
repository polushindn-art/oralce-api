package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import jakarta.persistence.Column
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import java.math.BigDecimal
import java.time.LocalDateTime

@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "PRCDOCHEAD", schema = Helper.SCHEME)
open class Prcdochead {
    @jakarta.persistence.Id
    @jakarta.persistence.Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @NotNull
    @Column(name = "CRN", nullable = false)
    open var crn: Long? = null

    @NotNull
    @Column(name = "DOCTYPE", nullable = false)
    open var docType: Long? = null

    @Size(max = 10)
    @NotNull
    @Column(name = "DOCPREF", nullable = false, length = 10)
    open var docpref: String? = null

    @NotNull
    @Column(name = "DOCDATE", nullable = false)
    open var docdate: LocalDateTime? = null

    @Column(name = "MANAGER", nullable = true)
    open var manager: Long? = null

    @Size(max = 80)
    @Column(name = "NOTE", length = 80)
    open var note: String? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "PRCDOC_STATUS", nullable = false)
    open var prcdocStatus: Long? = null

    @NotNull
    @Column(name = "DOCNUMB", nullable = false, precision = 17, scale = 2)
    open var docnumb: BigDecimal? = null

    @NotNull
    @ColumnDefault("1")
    @Column(name = "MODIFIED", nullable = false)
    open var modified: Long? = null

    @Column(name = "ORDERHEAD", nullable = true)
    var orderhead: Long? = null

    @Size(max = 6)
    @Column(name = "CHECK_ROZN_PRICE", length = 6)
    open var checkRoznPrice: String? = null

    @ColumnDefault("null")
    @Column(name = "PUSER")
    open var puser: Long? = null

}