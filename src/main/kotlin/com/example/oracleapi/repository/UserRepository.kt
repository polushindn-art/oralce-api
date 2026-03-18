package com.example.oracleapi.repository

import com.example.oracleapi.dto.user.UserList
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository : JpaRepository<UserList, Long> {
    fun findByusercode(usercode: String): Optional<UserList>
}