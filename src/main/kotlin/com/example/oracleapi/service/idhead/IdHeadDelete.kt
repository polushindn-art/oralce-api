package com.example.oracleapi.service.idhead

import com.example.oracleapi.dto.idhead.del.IdHeadDeleteRequest
import com.example.oracleapi.exception.DocumentNotFoundException
import com.example.oracleapi.repository.idhead.IdheadRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.ParameterMode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class IdHeadDelete(
    private val entityManager: EntityManager,
    private val idheadRepository: IdheadRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun delete(request: IdHeadDeleteRequest) {
        log.info("Удаление документа: rn={}, isUpdate={}", request.rn, request.isUpdate)

        // Проверка существования документа
        if (!idheadRepository.existsByRn(request.rn)) {
            log.warn("Документ с rn={} не найден", request.rn)
            throw DocumentNotFoundException(request.rn)
        }

        try {
            val storedProcedure = entityManager.createStoredProcedureQuery("PKG_IDHEAD.DEL")

            storedProcedure.registerStoredProcedureParameter("rn_", Long::class.java, ParameterMode.IN)
            storedProcedure.registerStoredProcedureParameter("isUpdate", Boolean::class.java, ParameterMode.IN)

            storedProcedure.setParameter("rn_", request.rn)
            storedProcedure.setParameter("isUpdate", request.isUpdate)

            storedProcedure.execute()

            log.info("Документ {} успешно удален", request.rn)

        } catch (e: Exception) {
            log.error("Ошибка при удалении документа {}: {}", request.rn, e.message)
            throw RuntimeException("Ошибка удаления документа: ${e.message}", e)
        }
    }
}