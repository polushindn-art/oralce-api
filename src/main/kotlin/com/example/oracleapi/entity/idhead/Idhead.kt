package com.example.oracleapi.entity.idhead

import com.example.oracleapi.Helper
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "IDHEAD", schema = Helper.SCHEME)
open class Idhead {
    @Id
    @Column(name = "RN", nullable = false, columnDefinition = "unknown")
    open var rn: Long? = null

    @Column(name = "CRN", nullable = false, columnDefinition = "unknown")
    open var crn: Long? = null

    @Column(name = "DOCTYPE", columnDefinition = "unknown")
    open var doctype: Long? = null

    @Column(name = "DOCPREF", columnDefinition = "unknown")
    open var docpref: String? = null

    @Column(name = "DOCDATE", columnDefinition = "unknown")
    open var docdate: Any? = null

    @Column(name = "STOREOUT", columnDefinition = "unknown")
    open var storeout: Any? = null

    @Column(name = "PROVIDER", columnDefinition = "unknown")
    open var provider: Any? = null

    @Column(name = "STOREIN", columnDefinition = "unknown")
    open var storein: Any? = null

    @Column(name = "NOTE", columnDefinition = "unknown")
    open var note: Any? = null

    @Column(name = "ID_STATUS", columnDefinition = "unknown")
    open var idStatus: Any? = null

    @Column(name = "DOCNUMB", columnDefinition = "unknown")
    open var docnumb: Any? = null

    @Column(name = "SUMPRICE", columnDefinition = "unknown")
    open var sumprice: Any? = null

    @Column(name = "MANAGER", columnDefinition = "unknown")
    open var manager: Any? = null

    @Column(name = "STOREOPER", columnDefinition = "unknown")
    open var storeoper: Any? = null

    @Column(name = "IDHEAD", columnDefinition = "unknown")
    open var idhead: Any? = null

    @Column(name = "ORDERHEAD", columnDefinition = "unknown")
    open var orderhead: Any? = null

    @Column(name = "INVHEAD", columnDefinition = "unknown")
    open var invhead: Any? = null

    @Column(name = "UL", columnDefinition = "unknown")
    open var ul: Any? = null

    @Column(name = "LOCKMANAGER", columnDefinition = "unknown")
    open var lockmanager: Any? = null

    @Column(name = "BASISDOCTYPE", columnDefinition = "unknown")
    open var basisdoctype: Any? = null

    @Column(name = "BASISDOCPREF", columnDefinition = "unknown")
    open var basisdocpref: Any? = null

    @Column(name = "BASISDOCDATE", columnDefinition = "unknown")
    open var basisdocdate: Any? = null

    @Column(name = "BASISDOCNUMB", columnDefinition = "unknown")
    open var basisdocnumb: Any? = null

    @Column(name = "MODIFIED", columnDefinition = "unknown")
    open var modified: Any? = null

    @Column(name = "TURNOVEROUT", columnDefinition = "unknown")
    open var turnoverout: Any? = null

    @Column(name = "TURNOVERIN", columnDefinition = "unknown")
    open var turnoverin: Any? = null

    @Column(name = "FACTINDATE", columnDefinition = "unknown")
    open var factindate: Any? = null

    @Column(name = "SUPPLIER", columnDefinition = "unknown")
    open var supplier: Any? = null

    @Column(name = "PRIDHEAD", columnDefinition = "unknown")
    open var pridhead: Any? = null

    @Column(name = "RIDHEAD", columnDefinition = "unknown")
    open var ridhead: Any? = null

    @Column(name = "TOHEAD", columnDefinition = "unknown")
    open var tohead: Any? = null

    @Column(name = "ORDERNAKLHEAD", columnDefinition = "unknown")
    open var ordernaklhead: Any? = null

    @Column(name = "PARTHEAD", columnDefinition = "unknown")
    open var parthead: Any? = null

    @Column(name = "SUMWEIGHT", columnDefinition = "unknown")
    open var sumweight: Any? = null

    @Column(name = "SUMVOLUME", columnDefinition = "unknown")
    open var sumvolume: Any? = null

    @Column(name = "NUMBTTN", columnDefinition = "unknown")
    open var numbttn: Any? = null

}