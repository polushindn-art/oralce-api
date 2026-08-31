package com.example.oracleapi.service.nomnlist

import com.example.oracleapi.dto.nomnlist.NomnlistDto
import com.example.oracleapi.repository.nomnlist.NomnlistRepository
import com.example.oracleapi.service.public.PublicGetNomenByBarcodeProcedure
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NomnlistService(
    private val nomnlistRepository: NomnlistRepository,
    private val nomnlistFind: NomnlistFind,
    private val publicGetNomenByBarcodeProcedure: PublicGetNomenByBarcodeProcedure
) {

    @Transactional(readOnly = true)
    fun existsByRn(rn: Long): Boolean {
        return nomnlistRepository.existsByRn(rn)
    }

    @Transactional(readOnly = true)
    fun findByRn(rn: Long): NomnlistDto? {
        return nomnlistFind.find(rn)
    }

    @Transactional(readOnly = true)
    fun findByBarcode(barcode: String): NomnlistDto? {
        val nomen = publicGetNomenByBarcodeProcedure.getNomenByBarcodeProcedure(barcode)
        return findByRn(nomen.nomen)
    }

}
