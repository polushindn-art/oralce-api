package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "NOMNLISTDATA", schema = Helper.Companion.SCHEME)
class Nomnlistdata {
    @Id
    @Column(name = "RN")
    var rn: Long = 0

    @Column(name = "NOMEN")
    var nomen: Long? = null

    @Lob
    @Column(name = "\"DATA\"", columnDefinition = "BLOB")
    var imageData: ByteArray? = null

    @Column(name = "PHOTONUM")
    var photonum: Int? = null

    @Column(name = "NEEDDOWNLOAD")
    var needdownload: Int? = null

    @Column(name = "MANAGER")
    var manager: Long? = null

    @Column(name = "CREATED")
    var created: LocalDateTime? = null

    @Column(name = "DELETED")
    var deleted: LocalDateTime? = null

    @Column(name = "DELUSER")
    var deluser: Long? = null

    @Column(name = "MD5")
    var md5: ByteArray? = null

    @Lob
    @Column(name = "MINIDATA", columnDefinition = "BLOB")
    var minidata: ByteArray? = null

    @Column(name = "HEIGHT")
    var height: Int? = null

    @Column(name = "WIDTH")
    var width: Int? = null

    @Column(name = "\"SIZE\"", columnDefinition = "NUMBER(7)")
    var fileSize: Long? = null
}