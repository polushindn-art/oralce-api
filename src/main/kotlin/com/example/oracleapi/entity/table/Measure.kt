package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault

@Entity
@Table(name = "MEASURE", schema = Helper.SCHEME)
open class Measure {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @Size(max = 20)
    @NotNull
    @Column(name = "MEASCODE", nullable = false, length = 20)
    open var meascode: String? = null

    @Size(max = 80)
    @NotNull
    @Column(name = "MEASNAME", nullable = false, length = 80)
    open var measname: String? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "TYPEMEAS", nullable = false)
    open var typemeas: Long? = null

    @Size(max = 80)
    @Column(name = "NOTE", length = 80)
    open var note: String? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "TAG2108", nullable = false)
    open var tag2108: Short? = null

}