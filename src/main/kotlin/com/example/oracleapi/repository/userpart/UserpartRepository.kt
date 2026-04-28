package com.example.oracleapi.repository.userpart

import com.example.oracleapi.entity.Userpart
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserpartRepository : JpaRepository<Userpart, Long> {

    // Найти все записи по RN пользователя
    fun findByUserrnRn(userRn: Long): List<Userpart>

}