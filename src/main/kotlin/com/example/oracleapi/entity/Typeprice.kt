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
@Table(name = "TYPEPRICE", schema = Helper.SCHEME)
open class Typeprice {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @Size(max = 20)
    @NotNull
    @Column(name = "PRICECODE", nullable = false, length = 20)
    open var pricecode: String? = null

    @Size(max = 80)
    @NotNull
    @Column(name = "PRICENAME", nullable = false, length = 80)
    open var pricename: String? = null

    @Size(max = 80)
    @Column(name = "NOTE", length = 80)
    open var note: String? = null

    @Column(name = "PICTURE")
    open var picture: ByteArray? = null

    @ColumnDefault("0")
    @Column(name = "ISUSED")
    open var isused: Boolean? = null

    @ColumnDefault("0")
    @Column(name = "ISUSEDTSD")
    open var isusedtsd: Boolean? = null

}