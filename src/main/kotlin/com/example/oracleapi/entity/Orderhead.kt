package com.example.oracleapi.entity

import com.example.oracleapi.Helper
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "ORDERHEAD", schema = Helper.SCHEME)
open class Orderhead {
    @Id
    @Column(name = "RN", nullable = false)
    open var id: Long? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "CRN", nullable = false)
    open var crn: Acatalog? = null

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
    @Column(name = "DOCNUMB", nullable = false, precision = 17, scale = 2)
    open var docnumb: BigDecimal? = null

    @NotNull
    @Column(name = "DOCDATE", nullable = false)
    open var docdate: LocalDate? = null

    @Column(name = "STATUSDATE1")
    open var statusdate1: LocalDate? = null

    @Column(name = "STATUSDATE2")
    open var statusdate2: LocalDate? = null

    @Column(name = "STATUSDATE3")
    open var statusdate3: LocalDate? = null

    @Column(name = "STATUSDATE4")
    open var statusdate4: LocalDate? = null

    @Column(name = "STATUSDATE5")
    open var statusdate5: LocalDate? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "PROVIDER", nullable = false)
    open var provider: AgnList? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "STOREIN", nullable = false)
    open var storein: Store? = null

    @Size(max = 640)
    @Column(name = "NOTE", length = 640)
    open var note: String? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "SUMPRICE", nullable = false, precision = 17, scale = 2)
    open var sumprice: BigDecimal? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "MANAGER", nullable = false)
    open var manager: Userlist? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @ColumnDefault("null")
    @JoinColumn(name = "UL", nullable = false)
    open var ul: AgnList? = null

    @NotNull
    @ColumnDefault("1")
    @Column(name = "MODIFIED", nullable = false)
    open var modified: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "OVERHEAD", nullable = false, precision = 17, scale = 5)
    open var overhead: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "SUMPRPRICE", nullable = false, precision = 17, scale = 2)
    open var sumprprice: BigDecimal? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "BASISDOCTYPE")
    open var basisdoctype: Typedoc? = null

    @Size(max = 10)
    @Column(name = "BASISDOCPREF", length = 10)
    open var basisdocpref: String? = null

    @Column(name = "BASISDOCDATE")
    open var basisdocdate: LocalDate? = null

    @Column(name = "BASISDOCNUMB", precision = 17, scale = 2)
    open var basisdocnumb: BigDecimal? = null

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "PRCDOCHEAD")
    open var prcdochead: Prcdochead? = null

    @ColumnDefault("0")
    @Column(name = "STATUS_FLAG")
    open var statusFlag: Long? = null

    @Column(name = "STATUSDATE8")
    open var statusdate8: LocalDate? = null

    @Column(name = "STATUSDATE9")
    open var statusdate9: LocalDate? = null

    @Column(name = "STATUSDATE10")
    open var statusdate10: LocalDate? = null

    @Column(name = "STATUSDATE11")
    open var statusdate11: LocalDate? = null

    @Column(name = "STATUSDATE12")
    open var statusdate12: LocalDate? = null

    @Column(name = "STATUSDATE13")
    open var statusdate13: LocalDate? = null

    @Column(name = "STATUSDATE14")
    open var statusdate14: LocalDate? = null

    @Column(name = "STATUSDATE15")
    open var statusdate15: LocalDate? = null

    @ColumnDefault("0")
    @Column(name = "TTIP")
    open var ttip: Long? = null

    @Size(max = 40)
    @Column(name = "NVAGON", length = 40)
    open var nvagon: String? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "TOPERATION", nullable = false)
    open var toperation: Boolean? = null

    @Column(name = "OPLATA", precision = 17, scale = 2)
    open var oplata: BigDecimal? = null

    @Size(max = 120)
    @Column(name = "NOTELOGIST", length = 120)
    open var notelogist: String? = null

    @Column(name = "SPECIALMARK")
    open var specialmark: Long? = null

    @Column(name = "ARRIVALDATE")
    open var arrivaldate: LocalDate? = null

    @Column(name = "STOREGATE")
    open var storegate: Long? = null

    @Column(name = "STATUSDATE16")
    open var statusdate16: LocalDate? = null

    @Column(name = "STATUSDATE17")
    open var statusdate17: LocalDate? = null

    @Column(name = "STATUSDATE18")
    open var statusdate18: LocalDate? = null

    @Column(name = "NACL_RASH")
    open var naclRash: Long? = null

    @Column(name = "MAX_PCENT")
    open var maxPcent: Double? = null

    @ColumnDefault("0")
    @Column(name = "NUMBTTN")
    open var numbttn: Long? = null

    @ColumnDefault("null")
    @Column(name = "TSD_USERLIST")
    open var tsdUserlist: Long? = null

    @ColumnDefault("0")
    @Column(name = "BANNED_PAYMENTS")
    open var bannedPayments: Long? = null

    @Column(name = "PLAN_ARRIVAL_DATE")
    open var planArrivalDate: LocalDate? = null

    @Size(max = 120)
    @Column(name = "NOMENTYPE", length = 120)
    open var nomentype: String? = null

    @Size(max = 120)
    @Column(name = "PACKTYPE", length = 120)
    open var packtype: String? = null

}