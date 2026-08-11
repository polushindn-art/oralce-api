package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import com.example.oracleapi.entity.table.Store
import com.example.oracleapi.entity.table.Typedoc
import com.example.oracleapi.entity.table.Userlist
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.JoinColumnOrFormula
import org.hibernate.annotations.JoinFormula
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "IDHEAD", schema = Helper.SCHEME)
open class Idhead {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long = 0

    @Column(name = "CRN", nullable = false)
    open var crn: Long = 0

    @Column(name = "DOCTYPE")
    open var doctype: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOCTYPE", insertable = false, updatable = false)
    open var doctypeEntity: Typedoc? = null

    @Column(name = "DOCPREF", nullable = false, length = 10)
    open var docpref: String? = null

    @Column(name = "DOCDATE", nullable = false)
    open var docdate: LocalDateTime? = null

    @Column(name = "STOREOUT")
    open var storeout: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STOREOUT", insertable = false, updatable = false)
    open var storeOutEntity: Store? = null

    @Column(name = "PROVIDER")
    open var provider: Long? = null

    @Column(name = "STOREIN")
    open var storein: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STOREIN", insertable = false, updatable = false)
    open var storeInEntity: Store? = null

    @Column(name = "NOTE", length = 80)
    open var note: String? = null

    @Column(name = "ID_STATUS", nullable = false)
    open var idStatus: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnOrFormula(
        column = JoinColumn(
            name = "ID_STATUS",
            referencedColumnName = "FIELD_VALUE",
            insertable = false,
            updatable = false
        )
    )
    @JoinColumnOrFormula(formula = JoinFormula(value = "'ID_STATUS'", referencedColumnName = "FIELD_NAME"))
    var statusEntity: Field? = null

    @Column(name = "DOCNUMB", nullable = false, precision = 17, scale = 2)
    open var docnumb: BigDecimal? = null

    @Column(name = "SUMPRICE", nullable = false, precision = 17, scale = 2)
    open var sumprice: BigDecimal? = null

    @Column(name = "MANAGER", nullable = false)
    open var manager: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MANAGER", insertable = false, updatable = false)
    var userListEntity: Userlist? = null

    @Column(name = "STOREOPER", nullable = false)
    open var storeoper: Long? = null

    @Column(name = "IDHEAD")
    open var idhead: Long? = null

    @Column(name = "ORDERHEAD")
    open var orderhead: Long? = null

    @Column(name = "INVHEAD")
    open var invhead: Long? = null

    @Column(name = "UL")
    open var ul: Long? = null

    @Column(name = "LOCKMANAGER")
    open var lockmanager: Long? = null

    @Column(name = "BASISDOCTYPE")
    open var basisdoctype: Long? = null

    @Column(name = "BASISDOCPREF", length = 10)
    open var basisdocpref: String? = null

    @Column(name = "BASISDOCDATE")
    open var basisdocdate: LocalDateTime? = null

    @Column(name = "BASISDOCNUMB", precision = 17, scale = 2)
    open var basisdocnumb: BigDecimal? = null

    @Column(name = "MODIFIED", nullable = false)
    open var modified: Long? = null

    @Column(name = "TURNOVEROUT", nullable = false)
    open var turnoverout: Long? = null

    @Column(name = "TURNOVERIN", nullable = false)
    open var turnoverin: Long? = null

    @Column(name = "FACTINDATE")
    open var factindate: LocalDateTime? = null

    @Column(name = "SUPPLIER")
    open var supplier: Long? = null

    @Column(name = "PRIDHEAD")
    open var pridhead: Long? = null

    @Column(name = "RIDHEAD")
    open var ridhead: Long? = null

    @Column(name = "TOHEAD")
    open var tohead: Long? = null

    @Column(name = "ORDERNAKLHEAD")
    open var ordernaklhead: Long? = null

    @Column(name = "PARTHEAD")
    open var parthead: Long? = null

    @Column(name = "SUMWEIGHT", precision = 17, scale = 3)
    open var sumweight: BigDecimal? = null

    @Column(name = "SUMVOLUME", precision = 17, scale = 3)
    open var sumvolume: BigDecimal? = null

    @Column(name = "NUMBTTN")
    open var numbttn: Long? = null
}