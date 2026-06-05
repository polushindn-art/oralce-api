package com.example.oracleapi.service.nomncat

import com.example.oracleapi.entity.table.NomncatRepository
import org.springframework.stereotype.Service

@Service
class NomnCatService(
    private val nomncatRepository: NomncatRepository
) {
    fun existByRn(rn: Long): Boolean {
        return nomncatRepository.existsById(rn)
    }
}