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
@Table(name = "TSDPARAM", schema = Helper.Companion.SCHEME)
open class Tsdparam {
    @Id
    @NotNull
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @NotNull
    @Column(name = "PRN", nullable = false)
    open var prn: Long? = null

    @Size(max = 20)
    @NotNull
    @Column(name = "PARAMNAME", nullable = false, length = 20)
    open var paramname: String? = null

    @Size(max = 160)
    @NotNull
    @Column(name = "PARAMVALUE", nullable = false, length = 160)
    open var paramvalue: String? = null

    @Size(max = 160)
    @ColumnDefault("NULL")
    @Column(name = "DESCRIPTION", length = 160)
    open var description: String? = null

}