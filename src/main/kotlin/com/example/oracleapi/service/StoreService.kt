package com.example.oracleapi.service

import com.example.oracleapi.dto.tsdlist.StoreInfo
import com.example.oracleapi.repository.store.StoreRepository
import org.springframework.stereotype.Service

@Service
class StoreService(
    private val storeRepository: StoreRepository
) {
    fun getStoresByPbeRn(pbeRn: Long): List<StoreInfo> {
        return storeRepository.findByStorepbe(pbeRn)
    }

    fun getStoresByPbeRnAndNote(pbeRn: Long): List<StoreInfo> {
        return storeRepository.findByStorepbeAndNote(pbeRn,"#ТСД")
    }
}