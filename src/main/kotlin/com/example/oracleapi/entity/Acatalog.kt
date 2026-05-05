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
@Table(name = "ACATALOG", schema = Helper.SCHEME)
open class Acatalog {
    @Id
    @Column(name = "RN", nullable = false)
    open var id: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "CRN", nullable = false)
    open var crn: Long? = null

    @Size(max = 160)
    @NotNull
    @Column(name = "NAME", nullable = false, length = 160)
    open var name: String? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "DIVISION", nullable = false)
    open var division: Division? = null

}