package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
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
@Table(name = "NOMNLISTP", schema = Helper.Companion.SCHEME)
open class Nomnlistp {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "NOMEN", nullable = false)
    open var nomen: Nomnlist? = null

    @Size(max = 80)
    @NotNull
    @Column(name = "NOMENCODE", nullable = false, length = 80)
    open var nomencode: String? = null

    @Size(max = 160)
    @NotNull
    @Column(name = "NOMENNAME", nullable = false, length = 160)
    open var nomenname: String? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "PROVIDER", nullable = false)
    open var provider: AgnList? = null

    @Size(max = 80)
    @Column(name = "NOTE", length = 80)
    open var note: String? = null

    @NotNull
    @ColumnDefault("1")
    @Column(name = "MASTER", nullable = false)
    open var master: Long? = null

}