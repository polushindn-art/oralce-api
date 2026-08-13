package com.example.oracleapi.entity.table

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "WWWHEAD", schema = "QREAL")
open class Wwwhead {
    @Id
    @Column(name = "RN", nullable = false)
    open var id: Long? = null

    @Size(max = 20)
    @NotNull
    @Column(name = "DOCPREF", nullable = false, length = 20)
    open var docpref: String? = null

    @NotNull
    @Column(name = "DOCNUMB", nullable = false, precision = 17, scale = 2)
    open var docnumb: BigDecimal? = null

    @NotNull
    @Column(name = "DOCDATE", nullable = false)
    open var docdate: LocalDate? = null

    @Size(max = 80)
    @Column(name = "NOTE", length = 80)
    open var note: String? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "WWW_STATUS", nullable = false)
    open var wwwStatus: Long? = null

    @NotNull
    @ColumnDefault("1")
    @Column(name = "MODIFIED", nullable = false)
    open var modified: Long? = null

    @Column(name = "APHEAD", precision = 17, scale = 2)
    open var aphead: BigDecimal? = null

    @Column(name = "ID_WEBORDER", precision = 17, scale = 2)
    open var idWeborder: BigDecimal? = null

    @Column(name = "WEBORDER")
    open var weborder: Long? = null

    @Column(name = "STATUS_UPLOAD")
    open var statusUpload: Long? = null

    @Column(name = "STATUSDATE")
    open var statusdate: LocalDate? = null

    @Column(name = "SUMM", precision = 17, scale = 2)
    open var summ: BigDecimal? = null

    @Column(name = "TDISKONT")
    open var tdiskont: Long? = null

    @Column(name = "DISKONT", precision = 17, scale = 5)
    open var diskont: BigDecimal? = null

    @Column(name = "TYPEPAY")
    open var typepay: Long? = null

    @Column(name = "NDSRATE")
    open var ndsrate: Long? = null

    @Size(max = 1024)
    @Column(name = "ADDRESS", length = 1024)
    open var address: String? = null

    @Size(max = 50)
    @Column(name = "PHONE", length = 50)
    open var phone: String? = null

    @Column(name = "DELIVERYDATETIME")
    open var deliverydatetime: LocalDate? = null

}