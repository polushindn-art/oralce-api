package com.example.oracleapi.repository.idhead

import com.example.oracleapi.entity.Idhead
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface IdheadRepository : JpaRepository<Idhead, Long>, JpaSpecificationExecutor<Idhead> {
    fun existsByRn(rn: Long): Boolean
    fun findByIdStatus(idStatus: Long): List<Idhead>
    fun countAllBy(): Long
    fun findByIdStatusAndDoctype(status: Long, doctype: Long): List<Idhead>
    fun findByIdStatusAndDoctypeIn(status: Long, doctypes: List<Long>): List<Idhead>

    // Переопределяем метод из JpaRepository для пагинации
    override fun findAll(pageable: Pageable): Page<Idhead>

    // Поиск по статусу с пагинацией
    fun findByIdStatus(idStatus: Long, pageable: Pageable): Page<Idhead>

    // Поиск по статусу и типу документа с пагинацией
    fun findByIdStatusAndDoctype(status: Long, doctype: Long, pageable: Pageable): Page<Idhead>
}