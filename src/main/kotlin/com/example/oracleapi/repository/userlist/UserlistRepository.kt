package com.example.oracleapi.repository.userlist

import com.example.oracleapi.entity.table.Userlist
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserlistRepository : JpaRepository<Userlist, Long> {

    fun findByUsercode(usercode: String): Userlist?

    // Case-insensitive поиск (рекомендуемый)
    @Query("SELECT u FROM Userlist u WHERE UPPER(u.usercode) = UPPER(:usercode)")
    fun findByUsercodeIgnoreCase(@Param("usercode") usercode: String): Userlist?

    @Query("SELECT COUNT(u) > 0 FROM Userlist u WHERE UPPER(u.usercode) = UPPER(:usercode)")
    fun existsByUsercode(@Param("usercode") usercode: String): Boolean

    // Получить RN пользователя по usercode
    @Query("SELECT u.rn FROM Userlist u WHERE UPPER(u.usercode) = UPPER(:usercode)")
    fun findRnByUsercode(@Param("usercode") usercode: String): Long?

    // Найти пользователя по usercode и получить USERAGN
    @Query("SELECT u.useragn FROM Userlist u WHERE UPPER(u.usercode) = UPPER(:usercode)")
    fun findUserAgnByUsercode(@Param("usercode") usercode: String): Long?


}