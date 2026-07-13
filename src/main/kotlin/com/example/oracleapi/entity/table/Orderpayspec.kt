package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import com.example.oracleapi.entity.Nomngroup
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
@Table(name = "ORDERPAYSPEC", schema = Helper.SCHEME)
open class Orderpayspec {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @NotNull
    @Column(name = "PRN", nullable = false)
    open var prn: Long? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "NOMNGROUP", nullable = false)
    open var nomngroup: Nomngroup? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "SUMM", nullable = false, precision = 17, scale = 2)
    open var summ: BigDecimal? = null

    @ColumnDefault("1")
    @Column(name = "MODIFIED")
    open var modified: Long? = null

}