package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import com.example.oracleapi.entity.table.Typedoc
import com.example.oracleapi.entity.table.Userlist
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
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "PAYDOC", schema = Helper.SCHEME)
open class Paydoc {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "DOCTYPE", nullable = false)
    open var doctype: Typedoc? = null

    @Size(max = 10)
    @NotNull
    @Column(name = "DOCPREF", nullable = false, length = 10)
    open var docpref: String? = null

    @NotNull
    @Column(name = "DOCDATE", nullable = false)
    open var docdate: LocalDateTime? = null

    @Size(max = 80)
    @Column(name = "PAYINFO", length = 80)
    open var payinfo: String? = null

    @NotNull
    @Column(name = "PAYSUMM", nullable = false, precision = 17, scale = 2)
    open var paysumm: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "NDSRATE", nullable = false, precision = 17, scale = 2)
    open var ndsrate: BigDecimal? = null

    @Size(max = 320)
    @Column(name = "NOTE", length = 320)
    open var note: String? = null

    @NotNull
    @Column(name = "DOCNUMB", nullable = false, precision = 17, scale = 2)
    open var docnumb: BigDecimal? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "MANAGER", nullable = false)
    open var manager: Userlist? = null

    @Size(max = 10)
    @Column(name = "ACCOUNTCOR", length = 10)
    open var accountcor: String? = null

    @Size(max = 80)
    @Column(name = "APPLICATION", length = 80)
    open var application: String? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "PPAY_STATUS", nullable = false)
    open var ppayStatus: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "LOADSUMM", nullable = false, precision = 17, scale = 2)
    open var loadsumm: BigDecimal? = null

    @Size(max = 40)
    @Column(name = "PAYMENT", length = 40)
    open var payment: String? = null

    @NotNull
    @ColumnDefault("1")
    @Column(name = "MODIFIED", nullable = false)
    open var modified: Long? = null

    @Column(name = "NDSSUM", precision = 17, scale = 2)
    open var ndssum: BigDecimal? = null

    @Column(name = "BASESUM", precision = 17, scale = 2)
    open var basesum: BigDecimal? = null

}