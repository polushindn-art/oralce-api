package com.example.oracleapi.repository.tohead

import com.example.oracleapi.dto.tohead.ToheadDto
import com.example.oracleapi.entity.table.Idhead
import com.example.oracleapi.entity.table.Tohead
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface ToheadRepository : JpaRepository<Tohead, Long>, JpaSpecificationExecutor<Tohead> {
    fun findToheadByRn(rn: Long): Tohead

    // Переопределяем метод из JpaRepository для пагинации
    override fun findAll(pageable: Pageable): Page<Tohead>
}