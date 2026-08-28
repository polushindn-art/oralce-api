package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
@Table(name = "FIELD", schema = Helper.SCHEME)
open class Field {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long = 0

    @Size(max = 20)
    @NotNull
    @Column(name = "FIELD_NAME", nullable = false, length = 20)
    open var fieldName: String = ""

    @NotNull
    @Column(name = "FIELD_VALUE", nullable = false)
    open var fieldValue: Long = 0

    @Size(max = 80)
    @NotNull
    @Column(name = "FIELD_COMMENT", nullable = false, length = 80)
    open var fieldComment: String = ""

    @Size(max = 80)
    @Column(name = "NOTE", length = 80)
    open var note: String? = null

    companion object {
        const val AGNTYPE = "AGNTYPE"
        const val ORDER_STATUS = "ORDER_STATUS"
        const val PRCDOC_STATUS = "PRCDOC_STATUS"
        const val ID_STATUS = "ID_STATUS"

        const val FIELD_VALUE = "FIELD_VALUE"
        const val FIELD_NAME = "FIELD_NAME"
    }
}