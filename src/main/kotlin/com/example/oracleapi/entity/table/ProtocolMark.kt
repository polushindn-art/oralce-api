package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate

@Entity
@Table(name = "PROTOCOL_MARK", schema = Helper.SCHEME)
open class ProtocolMark {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @NotNull
    @Column(name = "TABLE_RN", nullable = false)
    open var tableRn: Long? = null

    @NotNull
    @Column(name = "EVENT_DATE", nullable = false)
    open var eventDate: LocalDate? = null

    @NotNull
    @Column(name = "ACTION", nullable = false)
    open var action: Short? = null

    @Size(max = 1024)
    @Column(name = "BEFORE", length = 1024)
    open var before: String? = null

    @Size(max = 1024)
    @Column(name = "AFTER", length = 1024)
    open var after: String? = null

    @Size(max = 100)
    @Column(name = "USER_NAME", length = 100)
    open var userName: String? = null

    @Size(max = 50)
    @Column(name = "USER_IP", length = 50)
    open var userIp: String? = null

    @Size(max = 128)
    @Column(name = "PROGRAMM", length = 128)
    open var programm: String? = null

    @Size(max = 500)
    @Column(name = "NOTE", length = 500)
    open var note: String? = null

}