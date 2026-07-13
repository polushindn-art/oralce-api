package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDate

@Entity
@Table(name = "DATAMATRIX", schema = Helper.SCHEME)
open class Datamatrix {
    @Id
    @Column(name = "RN", nullable = false)
    open var id: Long? = null

    @Size(max = 100)
    @NotNull
    @Column(name = "BARCODE", nullable = false, length = 100)
    open var barcode: String? = null

    @Size(max = 100)
    @NotNull
    @Column(name = "DATAMATRIX", nullable = false, length = 100)
    open var datamatrix: String? = null

    @NotNull
    @Column(name = "USERRN", nullable = false)
    open var userrn: Long? = null

    @NotNull
    @Column(name = "SCANDATE", nullable = false)
    open var scandate: LocalDate? = null

    @NotNull
    @Column(name = "RETURNDM", nullable = false)
    open var returndm: Long? = null

    @ColumnDefault("null")
    @Column(name = "DOCRETURN")
    open var docreturn: Long? = null

}