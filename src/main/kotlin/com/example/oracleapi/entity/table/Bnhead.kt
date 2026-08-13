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
@Table(name = "BNHEAD", schema = "QREAL")
open class Bnhead {
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
    @Column(name = "SUMPRICE", nullable = false, precision = 17, scale = 2)
    open var sumprice: BigDecimal? = null

    @Size(max = 80)
    @Column(name = "NOTE", length = 80)
    open var note: String? = null

    @NotNull
    @ColumnDefault("1")
    @Column(name = "MODIFIED", nullable = false)
    open var modified: Long? = null

}