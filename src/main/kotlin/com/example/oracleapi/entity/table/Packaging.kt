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
@Table(name = "PACKAGING", schema = Helper.Companion.SCHEME)
open class Packaging {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @Size(max = 20)
    @NotNull
    @Column(name = "PACKAGINGCODE", nullable = false, length = 20)
    open var packagingcode: String? = null

    @Size(max = 40)
    @NotNull
    @Column(name = "PACKAGINGNAME", nullable = false, length = 40)
    open var packagingname: String? = null

    @NotNull
    @Column(name = "WIDTH", nullable = false, precision = 8, scale = 2)
    open var width: BigDecimal? = null

    @NotNull
    @Column(name = "HEIGHT", nullable = false, precision = 8, scale = 2)
    open var height: BigDecimal? = null

    @NotNull
    @Column(name = "LENGTH", nullable = false, precision = 8, scale = 2)
    open var length: BigDecimal? = null

    @NotNull
    @Column(name = "WEIGHT", nullable = false, precision = 8, scale = 2)
    open var weight: BigDecimal? = null

    @Size(max = 640)
    @Column(name = "NOTE", length = 640)
    open var note: String? = null

}