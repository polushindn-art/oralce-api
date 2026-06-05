package com.example.oracleapi.entity

import com.example.oracleapi.Helper
import com.example.oracleapi.entity.table.Acatalog
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
@Table(name = "SKU", schema = Helper.SCHEME)
open class Sku {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "CRN")
    open var crn: Acatalog? = null

    @Size(max = 20)
    @NotNull
    @Column(name = "SKUCODE", nullable = false, length = 20)
    open var skucode: String? = null

    @Size(max = 100)
    @NotNull
    @Column(name = "SKUNAME", nullable = false, length = 100)
    open var skuname: String? = null

    @Column(name = "MINPRICE")
    open var minprice: Long? = null

    @Column(name = "MAXPRICE")
    open var maxprice: Long? = null

    @ColumnDefault("0")
    @Column(name = "PRICEONSKU")
    open var priceonsku: Boolean? = null

    @Column(name = "NAMEONSKU")
    open var nameonsku: Boolean? = null

}