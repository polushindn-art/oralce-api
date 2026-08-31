package com.example.oracleapi.repository.user

import com.example.oracleapi.dto.user.VUserListResponse
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository : JpaRepository<VUserListResponse, Long> {
    fun findByusercode(usercode: String): Optional<VUserListResponse>
}