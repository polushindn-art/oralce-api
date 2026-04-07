package com.example.oracleapi.repository.user

import com.example.oracleapi.dto.user.VUserList
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository : JpaRepository<VUserList, Long> {
    fun findByusercode(usercode: String): Optional<VUserList>
}