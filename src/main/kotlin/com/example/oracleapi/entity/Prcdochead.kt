package com.example.oracleapi.entity

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
@Table(name = "PRCDOCHEAD", schema = "QREAL")
open class Prcdochead {
    @Id
    @Column(name = "RN", nullable = false)
    open var id: Long? = null

    @Size(max = 10)
    @NotNull
    @Column(name = "DOCPREF", nullable = false, length = 10)
    open var docpref: String? = null

    @NotNull
    @Column(name = "DOCDATE", nullable = false)
    open var docdate: LocalDate? = null

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

    @Size(max = 6)
    @Column(name = "CHECK_ROZN_PRICE", length = 6)
    open var checkRoznPrice: String? = null

    @ColumnDefault("null")
    @Column(name = "PUSER")
    open var puser: Long? = null

}