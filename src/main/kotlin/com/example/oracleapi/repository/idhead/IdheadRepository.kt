package com.example.oracleapi.repository.idhead

import com.example.oracleapi.entity.table.Idhead
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface IdheadRepository : JpaRepository<Idhead, Long>, JpaSpecificationExecutor<Idhead> {
    fun existsByRn(rn: Long): Boolean
    fun findByIdStatus(idStatus: Long): List<Idhead>
    fun countAllBy(): Long
    fun countByIdStatus(idStatus: Long): Long

    // Переопределяем метод из JpaRepository для пагинации
    override fun findAll(pageable: Pageable): Page<Idhead>

    // Поиск по статусу с пагинацией
    fun findByIdStatus(idStatus: Long, pageable: Pageable): Page<Idhead>

}