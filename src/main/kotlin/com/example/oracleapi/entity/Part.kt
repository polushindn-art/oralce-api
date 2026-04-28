package com.example.oracleapi.entity

import com.example.oracleapi.Helper
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal

@Entity
@Table(name = "PART", schema = Helper.SCHEME)
open class Part {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @Size(max = 40)
    @NotNull
    @Column(name = "PARTCODE", nullable = false, length = 40)
    open var partcode: String? = null

    @Size(max = 80)
    @NotNull
    @Column(name = "PARTNAME", nullable = false, length = 80)
    open var partname: String? = null

    @Size(max = 80)
    @Column(name = "NOTE", length = 80)
    open var note: String? = null

    @Column(name = "MAXSUMM", precision = 17, scale = 2)
    open var maxsumm: BigDecimal? = null

}