package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import com.example.oracleapi.entity.table.Orderhead
import com.example.oracleapi.entity.table.Typedoc
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "ORDERNAKLHEAD", schema = Helper.SCHEME)
open class Ordernaklhead {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "PRN", nullable = false)
    open var prn: Orderhead? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "BASISDOCTYPE")
    open var basisdoctype: Typedoc? = null

    @Size(max = 10)
    @Column(name = "BASISDOCPREF", length = 10)
    open var basisdocpref: String? = null

    @Column(name = "BASISDOCNUMB", precision = 17, scale = 2)
    open var basisdocnumb: BigDecimal? = null

    @Column(name = "BASISDOCDATE")
    open var basisdocdate: LocalDate? = null

    @NotNull
    @Column(name = "PROVIDER", nullable = false)
    open var provider: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "SUMPRICE", nullable = false, precision = 17, scale = 2)
    open var sumprice: BigDecimal? = null

    @NotNull
    @ColumnDefault("1")
    @Column(name = "MODIFIED", nullable = false)
    open var modified: Long? = null

    @Column(name = "NUMBTTN")
    open var numbttn: Long? = null

}