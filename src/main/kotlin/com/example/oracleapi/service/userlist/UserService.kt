package com.example.oracleapi.service.userlist

import com.example.oracleapi.entity.table.Userlist
import com.example.oracleapi.repository.userlist.UserlistRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userlistRepository: UserlistRepository
) {
    fun checkUserExists(usercode: String): Boolean {
        return userlistRepository.existsByUsercode(usercode)
    }
}