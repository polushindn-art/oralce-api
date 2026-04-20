package com.example.oracleapi.entity

import com.example.oracleapi.Helper
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault

@Entity
@Table(name = "COUNTRY", schema = Helper.SCHEME)
open class Country {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @Size(max = 40)
    @NotNull
    @Column(name = "COUNTRYCODE", nullable = false, length = 40)
    open var countrycode: String? = null

    @Size(max = 80)
    @NotNull
    @Column(name = "COUNTRYNAME", nullable = false, length = 80)
    open var countryname: String? = null

    @NotNull
    @ColumnDefault("1")
    @Column(name = "GTD", nullable = false)
    open var gtd: Long? = null

    @Size(max = 80)
    @Column(name = "NOTE", length = 80)
    open var note: String? = null

}