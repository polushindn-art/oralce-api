package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import com.example.oracleapi.entity.Nomngroup
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "CONTRACT", schema = Helper.SCHEME)
open class Contract {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AGNLIST")
    open var agnlistEntity: AgnList? = null

    @Column(name = "AGNLIST", insertable = false, updatable = false )
    open var agnlist: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UL")
    open var ulEntity: AgnList? = null

    @Column(name = "UL", insertable = false, updatable = false)
    open var ul: Long? = null

    @Size(max = 64)
    @NotNull
    @Column(name = "NUM_CANTRACT", nullable = false, length = 64)
    open var numCantract: String? = null

    @NotNull
    @Column(name = "BEGINDATE", nullable = false)
    open var begindate: LocalDate? = null

    @Column(name = "ENDDATE")
    open var enddate: LocalDate? = null

    @ColumnDefault("0")
    @Column(name = "LIMITED")
    open var limited: Short? = null

    @ColumnDefault("1")
    @Column(name = "STATUS", precision = 17, scale = 2)
    open var status: BigDecimal? = null

    @Size(max = 320)
    @Column(name = "OBJECTADRESS", length = 320)
    open var objectadress: String? = null

    @Size(max = 160)
    @Column(name = "NOTE", length = 160)
    open var note: String? = null

    @ColumnDefault("null")
    @Column(name = "CONTRDATA")
    open var contrdata: ByteArray? = null

    @Size(max = 160)
    @ColumnDefault("NULL")
    @Column(name = "TYPESAVECONTR", length = 160)
    open var typesavecontr: String? = null

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRN", referencedColumnName = "RN", insertable = false, updatable = false)
    var specEntity: MutableList<Contractval> = mutableListOf()

}