package com.example.oracleapi.entity.table

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
@Table(name = "TYPEPAY", schema = "QREAL")
open class Typepay {
    @Id
    @Column(name = "RN", nullable = false)
    open var id: Long? = null

    @Size(max = 20)
    @NotNull
    @Column(name = "PAYCODE", nullable = false, length = 20)
    open var paycode: String? = null

    @Size(max = 80)
    @NotNull
    @Column(name = "PAYNAME", nullable = false, length = 80)
    open var payname: String? = null

    @Size(max = 80)
    @Column(name = "NOTE", length = 80)
    open var note: String? = null

}