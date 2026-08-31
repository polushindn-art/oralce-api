package com.example.oracleapi.service

import com.example.oracleapi.dto.store.StoreResponse
import com.example.oracleapi.dto.store.StoreSimpleResponse
import com.example.oracleapi.entity.table.Store
import com.example.oracleapi.repository.store.StoreRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StoreService(
    private val storeRepository: StoreRepository
) {
    @Transactional(readOnly = true)
    fun getAllStores(): List<StoreResponse> {
        return storeRepository.findAll().map { StoreResponse.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun getAllSortedByStorecodeAsc(): List<StoreResponse> {
        return storeRepository.findAllByOrderByStorecodeAsc().map { StoreResponse.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun getStoresByPbe(pbeRn: Long): List<StoreResponse> {
        return storeRepository.findByStorepbe(pbeRn)
            .map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getStoresByPbeAndNote(pbeRn: Long, note: String): List<StoreSimpleResponse> {
        return storeRepository.findByStorepbeAndNote(pbeRn, note)
            .map { StoreSimpleResponse.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun getAllStoresForTsd(): List<StoreResponse> {
        return storeRepository.findAll()
            .map { StoreResponse.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun getStoreByRn(rn: Long?): StoreResponse {
        if (rn != null) {
            return storeRepository
                .findById(rn)
                .map { StoreResponse.fromEntity(it) }
                .orElseThrow { IllegalArgumentException("Склад с RN=$rn не найден") }
        } else {
            throw IllegalArgumentException("Пустое значение склада")
        }
    }

    @Transactional(readOnly = true)
    fun getStoresWithPagination(pageable: Pageable): Page<Store> {
        return storeRepository.findAll(pageable)
    }

    @Transactional(readOnly = true)
    fun searchStores(query: String, pageable: Pageable): Page<Store> {
        return storeRepository.findByStorenameContainingIgnoreCaseOrStorecodeContainingIgnoreCase(
            query, query, pageable
        )
    }

    fun Store.toResponse(): StoreResponse {
        return StoreResponse.fromEntity(this)
    }

}