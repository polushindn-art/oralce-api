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
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
@Table(name = "TUNNING", schema = Helper.SCHEME)
open class Tunning {
    @Id
    @Column(name = "RN", nullable = false)
    open var id: Long? = null

    @Size(max = 40)
    @NotNull
    @Column(name = "PARAMNAME", nullable = false, length = 40)
    open var paramname: String? = null

    @Size(max = 160)
    @NotNull
    @Column(name = "PARAMVALUE", nullable = false, length = 160)
    open var paramvalue: String? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "USERRN", nullable = false)
    open var userrn: Userlist? = null

}