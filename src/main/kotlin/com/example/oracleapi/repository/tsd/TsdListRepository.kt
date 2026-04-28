package com.example.oracleapi.repository.tsd

import com.example.oracleapi.entity.Tsdlist
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface TsdListRepository : JpaRepository<Tsdlist, Long> {

    fun existsBySn(sn: String): Boolean

    fun findBySn(sn: String): Tsdlist?

    @Query("""
    SELECT t FROM Tsdlist t
    LEFT JOIN FETCH t.params
    WHERE t.sn = :sn
    """)
    fun findTerminalWithParams(@Param("sn") sn: String): Tsdlist?

    // Поиск по Device ID
    fun findByDeviceid(deviceId: String): Tsdlist?

    // Проверка существования по Device ID
    fun existsByDeviceid(deviceId: String): Boolean

}