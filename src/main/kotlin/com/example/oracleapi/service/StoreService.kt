package com.example.oracleapi.service

import com.example.oracleapi.dto.store.StoreResponse
import com.example.oracleapi.dto.store.StoreSimpleResponse
import com.example.oracleapi.entity.Store
import com.example.oracleapi.repository.store.StoreRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class StoreService(
    private val storeRepository: StoreRepository
) {
    fun getAllStores(): List<StoreResponse> {
        return storeRepository.findAll().map { StoreResponse.fromEntity(it) }
    }

    fun getAllSortedByStorecodeAsc(): List<StoreResponse> {
        return storeRepository.findAllByOrderByStorecodeAsc().map { StoreResponse.fromEntity(it) }
    }

    fun getStoresByPbe(pbeRn: Long): List<StoreResponse> {
        return storeRepository.findByStorepbe(pbeRn)
            .map { it.toResponse() }
    }

    fun getStoresByPbeAndNote(pbeRn: Long, note: String): List<StoreSimpleResponse> {
        return storeRepository.findByStorepbeAndNote(pbeRn, note)
            .map { StoreSimpleResponse.fromEntity(it) }
    }

    fun getAllStoresForTsd(): List<StoreResponse> {
        return storeRepository.findAll()
            .map { StoreResponse.fromEntity(it) }
    }

    fun getStoreByRn(rn: Long): StoreResponse {
        return storeRepository
            .findById(rn)
            .map { StoreResponse.fromEntity(it) }
            .orElseThrow { IllegalArgumentException("Склад с RN=$rn не найден") }
    }

    fun getStoresWithPagination(pageable: Pageable): Page<Store> {
        return storeRepository.findAll(pageable)
    }

    fun searchStores(query: String, pageable: Pageable): Page<Store> {
        return storeRepository.findByStorenameContainingIgnoreCaseOrStorecodeContainingIgnoreCase(
            query, query, pageable
        )
    }

    fun Store.toResponse(): StoreResponse {
        return StoreResponse.fromEntity(this)
    }

}