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
@Table(name = "AGNDSCCARD", schema = "QREAL")
open class Agndsccard {
    @Id
    @Column(name = "RN", nullable = false)
    open var id: Long? = null

    @Size(max = 20)
    @NotNull
    @Column(name = "DSCBARNUMB", nullable = false, length = 20)
    open var dscbarnumb: String? = null

    @NotNull
    @ColumnDefault("sysdate")
    @Column(name = "DSCDATE", nullable = false)
    open var dscdate: LocalDate? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "DSCCARDTYPE", nullable = false)
    open var dsccardtype: Long? = null

    @NotNull
    @Column(name = "MAXSUMM", nullable = false, precision = 17, scale = 2)
    open var maxsumm: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "DSCLOCK", nullable = false)
    open var dsclock: Long? = null

    @Size(max = 80)
    @Column(name = "NOTE", length = 80)
    open var note: String? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "BONUS", nullable = false, precision = 17, scale = 2)
    open var bonus: BigDecimal? = null

    @Size(max = 20)
    @NotNull
    @Column(name = "RESPONCODE", nullable = false, length = 20)
    open var responcode: String? = null

    @Column(name = "BASISTOHEAD")
    open var basistohead: Long? = null

    @Column(name = "TOHEAD")
    open var tohead: Long? = null

    @Column(name = "RESPONCEAGN")
    open var responceagn: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "INTERNETTORG", nullable = false)
    open var internettorg: Boolean? = null

    @Column(name = "PHONENUMBERRN")
    open var phonenumberrn: Long? = null

}