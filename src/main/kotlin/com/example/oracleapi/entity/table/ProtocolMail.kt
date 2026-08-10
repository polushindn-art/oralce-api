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
@Table(name = "PROTOCOL_MAIL", schema = Helper.Companion.SCHEME)
open class ProtocolMail {
    @Id
    @NotNull
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @NotNull
    @Column(name = "DATE_", nullable = false)
    open var date: LocalDate? = null

    @ColumnDefault("0")
    @Column(name = "RESULT")
    open var result: Long? = null

    @Column(name = "MESSAGE")
    open var message: ByteArray? = null

    @Size(max = 320)
    @Column(name = "ERRNAME", length = 320)
    open var errname: String? = null

    @ColumnDefault("null")
    @Column(name = "TABLE_RN")
    open var tableRn: Long? = null

    @Size(max = 64)
    @ColumnDefault("null")
    @Column(name = "TABLE_NAME", length = 64)
    open var tableName: String? = null

    @Size(max = 64)
    @ColumnDefault("null")
    @Column(name = "USER_", length = 64)
    open var user: String? = null

    @Size(max = 128)
    @ColumnDefault("null")
    @Column(name = "HOST_", length = 128)
    open var host: String? = null

    @Size(max = 32)
    @ColumnDefault("null")
    @Column(name = "IP_", length = 32)
    open var ip: String? = null

}