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
@Table(name = "DIVISION", schema = Helper.SCHEME)
open class Division {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @Size(max = 20)
    @NotNull
    @Column(name = "DIVISIONCODE", nullable = false, length = 20)
    open var divisioncode: String? = null

    @Size(max = 80)
    @NotNull
    @Column(name = "DIVISIONNAME", nullable = false, length = 80)
    open var divisionname: String? = null

    @Size(max = 80)
    @Column(name = "NOTE", length = 80)
    open var note: String? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "CRN", nullable = false)
    open var crn: Acatalog? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "ISTYPEDOC", nullable = false)
    open var istypedoc: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "ISSAMPLEDOC", nullable = false)
    open var issampledoc: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "ISREADWRITE", nullable = false)
    open var isreadwrite: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "ISADMIN", nullable = false)
    open var isadmin: Long? = null

}