package com.example.oracleapi.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "PICTURE")
class Picture(
    @Id
    @Column(name = "RN")
    var rn: Long = 0,

    @Lob
    @Column(name = "PICTURE")
    var picture: ByteArray = byteArrayOf(),

    @Column(name = "DATATYPE")
    var datatype: String = "",

    @Column(name = "TABLENAME")
    var tablename: String = "",

    @Column(name = "TABLERN")
    var tablern: Long = 0,

    @Column(name = "EVENTDATE")
    var eventdate: LocalDateTime = LocalDateTime.now(),

    @Lob
    @Column(name = "PREVIEW")
    var preview: ByteArray? = null,

    @Column(name = "DELETED")
    var deleted: LocalDateTime? = null
)