package com.example.oracleapi.entity.table

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Size

@Entity
@Table(name = "MARK_LINK", schema = "QREAL")
open class MarkLink {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @Column(name = "PRN", nullable = false)
    open var prn: Long? = null

    @Column(name = "SPEC_RN")
    open var specRn: Long? = null

    @Size(max = 64)
    @Column(name = "TABLE_NAME", length = 64)
    open var tableName: String? = null

}