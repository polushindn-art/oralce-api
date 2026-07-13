package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal

@Entity
@Table(name = "AUTOMARGINOC", schema = Helper.Companion.SCHEME)
open class Automarginoc {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @Size(max = 3)
    @NotNull
    @Column(name = "MARGINCODE", nullable = false, length = 3)
    open var margincode: String? = null

    @NotNull
    @Column(name = "PERCENT", nullable = false, precision = 5, scale = 3)
    open var percent: BigDecimal? = null

}