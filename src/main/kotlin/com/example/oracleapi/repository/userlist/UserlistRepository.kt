package com.example.oracleapi.repository.userlist

import com.example.oracleapi.entity.userlist.Userlist
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserlistRepository : JpaRepository<Userlist, Long> {

    @Query("SELECT COUNT(u) > 0 FROM Userlist u WHERE UPPER(u.usercode) = UPPER(:usercode)")
    fun existsByUsercode(@Param("usercode") usercode: String): Boolean
}