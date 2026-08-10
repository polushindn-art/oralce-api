package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
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
@Table(name = "store", schema = Helper.SCHEME)
data class Store(
    @Id
    @Column(name = "rn")
    val rn: Long,
    @Column(name = "STOREPBE", nullable = false)
    val storepbe: Long,
    @Column(name = "STORECODE", nullable = false, unique = true)
    val storecode: String,
    @Column(name = "STORENAME", nullable = false)
    val storename: String,
    @Size(max = 20)
    @NotNull
    @Column(name = "STORENUMB", nullable = false, length = 20)
    var storenumb: String? = null,
    @Size(max = 80)
    @Column(name = "NOTE", length = 80)
    var note: String? = null,
    @ColumnDefault("NULL")
    @Column(name = "WORKINDEX")
    var workindex: Long? = null,
    @NotNull
    @ColumnDefault("0")
    @Column(name = "RESERVE", nullable = false)
    var reserve: Long? = null,
    @Column(name = "COLINDEX")
    var colindex: Long? = null,
    @Size(max = 20)
    @Column(name = "PR_LOGIST", length = 20)
    var prLogist: String? = null,
    @ColumnDefault("0")
    @Column(name = "AUTOZ")
    var autoz: Long? = null,
    @Column(name = "S")
    var s: Long? = null,
    @ColumnDefault("0")
    @Column(name = "STOREOC")
    var storeoc: Boolean? = null,
    @Column(name = "QMAX", precision = 12, scale = 3)
    var qmax: BigDecimal? = null,
    @Column(name = "QMAXCUR", precision = 12, scale = 3)
    var qmaxcur: BigDecimal? = null,
    @Column(name = "QCUR", precision = 12, scale = 3)
    var qcur: BigDecimal? = null,
    @Column(name = "QCUR_0", precision = 12, scale = 3)
    var qcur0: BigDecimal? = null,
    @Column(name = "VMAX", precision = 15, scale = 6)
    var vmax: BigDecimal? = null,
    @Column(name = "VCUR", precision = 15, scale = 6)
    var vcur: BigDecimal? = null,
    @Column(name = "VCUR_0", precision = 15, scale = 6)
    var vcur0: BigDecimal? = null,
    @ColumnDefault("0")
    @Column(name = "USESAS")
    var usesas: Long? = null,
    @ColumnDefault("0")
    @Column(name = "EQUEUE")
    var equeue: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "STORERAZBOR")
    var storerazbor: Store? = null,
    @Column(name = "CANNEGATIVE")
    var cannegative: Long? = null,
    @ColumnDefault("0")
    @Column(name = "ESCHEMA")
    var eschema: Long? = null,
    @ColumnDefault("0")
    @Column(name = "CANBASKET")
    var canbasket: Long? = null,
    @ColumnDefault("1")
    @Column(name = "ISVISIBLE")
    var isvisible: Boolean? = null,
    @ColumnDefault("NULL")
    @Column(name = "WEBSTORE")
    var webstore: Boolean? = null)