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
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
@Table(name = "AGNPHONENUMBERLIST", schema = Helper.SCHEME)
open class Agnphonenumberlist {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @NotNull
    @Column(name = "PRNAGN", nullable = false)
    open var prnagn: Long? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRNAGN", nullable = false, updatable = false, insertable = false)
    open var prnagnEntity: AgnList? = null

    @Size(max = 20)
    @NotNull
    @Column(name = "PHONENUMBER", nullable = false, length = 20)
    open var phonenumber: String? = null

    @Size(max = 100)
    @Column(name = "NOTE", length = 100)
    open var note: String? = null

    @Size(max = 20)
    @Column(
        name = "PHONE_TAIL",
        length = 20,
        insertable = false,
        updatable = false,
    )
    open var phoneTail: String? = null

}