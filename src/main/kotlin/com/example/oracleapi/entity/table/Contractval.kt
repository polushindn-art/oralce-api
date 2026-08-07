package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import com.example.oracleapi.entity.table.Nomngroup
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.math.BigDecimal

@Entity
@Table(name = "CONTRACTVAL", schema = Helper.SCHEME)
open class Contractval {

    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @Column(name = "prn", insertable = false, updatable = false)
    open var prn: Long? = null

    @Column(name = "DELIVERY_DAYS", precision = 17, scale = 2)
    open var deliveryDays: BigDecimal? = null

    @Column(name = "ORDERPAY", precision = 17, scale = 2)
    open var orderpay: BigDecimal? = null

    @Column(name = "CALENDAR_DAY", precision = 17, scale = 2)
    open var calendarDay: BigDecimal? = null

    @Column(name = "nomngroup", insertable = false, updatable = false)
    open var nomngroup: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "NOMNGROUP")
    open var nomngroupEntity: Nomngroup? = null

    @ColumnDefault("0")
    @Column(name = "CHECKDI")
    open var checkdi: Boolean? = null

}