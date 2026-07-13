package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import com.example.oracleapi.entity.Orderhead
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.JoinFormula
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.Where
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "ORDERPAYHEAD", schema = Helper.SCHEME)
open class Orderpayhead {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @Column(name = "ORDERHEAD", nullable = false, insertable = false, updatable = false)
    open var orderhead: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "ORDERHEAD")
    open var orderheadEntity: Orderhead? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "STATUS", nullable = false)
    open var status: Long? = null

    @NotNull
    @Column(name = "DOCDATE", nullable = false)
    open var docdate: LocalDate? = null

    @NotNull
    @Column(name = "PAYDOC", nullable = false, insertable = false, updatable = false)
    open var paydoc: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYDOC")
    open var paydocEntity: Paydoc? = null

    @Column(name = "PLANDATE")
    open var plandate: LocalDate? = null

    @Size(max = 1000)
    @Column(name = "NOTE", length = 1000)
    open var note: String? = null

    @Column(name = "ORDERPAY", precision = 17, scale = 2)
    open var orderpay: BigDecimal? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula(
        value = """
            (SELECT f.RN 
            FROM ${Helper.SCHEME}.field f 
            WHERE f.FIELD_NAME = 'PREPAY' 
              AND f.FIELD_VALUE = ORDERPAY)
        """
    )
    var prepayField: Field? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "NUM_CANTRACT")
    open var numCantract: Contract? = null

    @ColumnDefault("1")
    @Column(name = "MODIFIED")
    open var modified: Long? = null

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRN", referencedColumnName = "RN", insertable = false, updatable = false)
    var specEntity: MutableList<Orderpayspec> = mutableListOf()

}