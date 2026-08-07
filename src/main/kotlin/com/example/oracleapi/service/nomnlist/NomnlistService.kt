package com.example.oracleapi.service.nomnlist

import com.example.oracleapi.dto.nomnlist.NomnlistDto
import com.example.oracleapi.repository.nomnlist.NomnlistRepository
import com.example.oracleapi.service.public.PublicGetNomenByBarcodeProcedure
import org.springframework.stereotype.Service

@Service
class NomnlistService(
    private val nomnlistRepository: NomnlistRepository,
    private val nomnlistFind: NomnlistFind,
    private val publicGetNomenByBarcodeProcedure: PublicGetNomenByBarcodeProcedure
) {
    fun existsByRn(rn: Long): Boolean {
        return nomnlistRepository.existsByRn(rn)
    }

    fun findByRn(rn: Long): NomnlistDto? {
        return nomnlistFind.find(rn)
    }

    fun findByBarcode(baroce: String): NomnlistDto? {
        val nomen = publicGetNomenByBarcodeProcedure.getNomenByBarcodeProcedure(baroce)
        return findByRn(nomen.nomen)
    }

}
