package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import com.example.oracleapi.entity.table.Nomnlist
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.math.BigDecimal

@Entity
@Table(name = "ASSORTSPEC", schema = Helper.Companion.SCHEME)
open class Assortspec {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "NOMEN", nullable = false)
    open var nomen: Nomnlist? = null

    @NotNull
    @Column(name = "KOEF", nullable = false, precision = 17, scale = 10)
    open var koef: BigDecimal? = null

    @Column(name = "QUANTSUP", precision = 17, scale = 3)
    open var quantsup: BigDecimal? = null

    @Column(name = "PERIODSUP", precision = 17, scale = 1)
    open var periodsup: BigDecimal? = null

    @Column(name = "BASIC")
    open var basic: Long? = null

    @Column(name = "VEHICLE")
    open var vehicle: Long? = null

    @Column(name = "DELIVERYCS")
    open var deliverycs: Long? = null

    @Column(name = "DELIVERY1")
    open var delivery1: Long? = null

    @Column(name = "DELIVERY2")
    open var delivery2: Long? = null

    @Column(name = "DELIVERY3")
    open var delivery3: Long? = null

    @Column(name = "DELIVERY4")
    open var delivery4: Long? = null

    @Size(max = 80)
    @Column(name = "NOTE", length = 80)
    open var note: String? = null

    @Column(name = "NOMENCS")
    open var nomencs: Long? = null

    @Column(name = "NOMEN1")
    open var nomen1: Long? = null

    @Column(name = "NOMEN2")
    open var nomen2: Long? = null

    @Column(name = "NOMEN3")
    open var nomen3: Long? = null

    @Column(name = "NOMEN4")
    open var nomen4: Long? = null

    @NotNull
    @Column(name = "COEF", nullable = false, precision = 17, scale = 10)
    open var coef: BigDecimal? = null

}