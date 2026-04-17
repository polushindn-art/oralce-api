package com.example.oracleapi.entity

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
@Table(name = "MEASUNITP", schema = Helper.SCHEME)
open class Measunitp {
    @Id
    @Column(name = "RN")
    open var rn: Long? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "NOMNLISTP", nullable = false)
    open var nomnlistp: Nomnlistp? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "MEASUNIT", nullable = false)
    open var measunit: Measunit? = null

    @Column(name = "ISSHIP")
    open var isship: Long? = null

    @Column(name = "ISACTIVE")
    open var isactive: Long? = null

    @Size(max = 80)
    @Column(name = "NOTE", length = 80)
    open var note: String? = null

    @ColumnDefault("0")
    @Column(name = "CHECKED")
    open var checked: Boolean? = null

}