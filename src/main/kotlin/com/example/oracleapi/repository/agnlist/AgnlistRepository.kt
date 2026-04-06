package com.example.oracleapi.repository.agnlist

import com.example.oracleapi.entity.agnlist.Agnlist
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AgnlistRepository : JpaRepository<Agnlist, Long> {

    // Поиск по мнемокоду (AGNCODE)
    fun findByAgncode(agncode: String): Agnlist?

    // Проверка существования по мнемокоду
    fun existsByAgncode(agncode: String): Boolean

    // Поиск по ИНН
    fun findByAgnidnumb(agnidnumb: String): List<Agnlist>

    // Поиск по наименованию (частичное совпадение)
    @Query("SELECT a FROM Agnlist a WHERE a.agnname LIKE %:name%")
    fun findByAgnnameContaining(@Param("name") name: String): List<Agnlist>

    // Поиск активных контрагентов
    @Query("SELECT a FROM Agnlist a WHERE a.enabled = 1 AND a.agncode = :agncode")
    fun findActiveByAgncode(@Param("agncode") agncode: String): Agnlist?
}