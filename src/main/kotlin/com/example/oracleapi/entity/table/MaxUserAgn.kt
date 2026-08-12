package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import com.example.oracleapi.annotation.BindingDateTimeFormat
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import java.time.Instant
import java.time.LocalDateTime

@Entity
@Table(name = "MAX_USER_AGN", schema = Helper.SCHEME)
open class MaxUserAgn {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @Size(max = 100)
    @NotNull
    @Column(name = "USER_ID", nullable = false, length = 100)
    open var userId: String? = null

    @Size(max = 100)
    @NotNull
    @Column(name = "CHAT_ID", nullable = false, length = 100)
    open var chatId: String? = null

    @Size(max = 500)
    @Column(name = "USER_NAME", length = 500)
    open var userName: String? = null

    @Size(max = 50)
    @NotNull
    @Column(name = "PHONE", nullable = false, length = 50)
    open var phone: String? = null

    @Column(name = "PHONE_TAIL", length = 20, insertable = false, updatable = false)
    var phoneTail: String? = null

    @Size(max = 50)
    @ColumnDefault("'MAIN'")
    @Column(name = "BOT_TYPE", length = 50)
    open var botType: String? = null

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "CREATED_AT")
    open var createdAt: LocalDateTime = LocalDateTime.now()

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "UPDATED_AT")
    open var updatedAt: LocalDateTime = LocalDateTime.now()

    @ColumnDefault("1")
    @Column(name = "IS_ACTIVE")
    open var isActive: Boolean? = null

    @Lob
    @Column(name = "AVATAR")
    var avatar: ByteArray? = null

}