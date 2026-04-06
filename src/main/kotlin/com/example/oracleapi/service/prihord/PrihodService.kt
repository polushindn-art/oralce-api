package com.example.oracleapi.service.prihord

import com.example.oracleapi.dto.prihod.PrihodRequest
import com.example.oracleapi.repository.agnlist.AgnlistRepository
import com.example.oracleapi.repository.store.StoreRepository
import com.example.oracleapi.repository.typedoc.TypedocRepository
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityManager
import jakarta.persistence.ParameterMode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PrihodService(
    private val entityManager: EntityManager,
    private val objectMapper: ObjectMapper,
    private val storeRepository: StoreRepository,
    private val typedocRepository: TypedocRepository,
    private val agnlistRepository: AgnlistRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)
    @Transactional
    fun createPrihodByJson(request: PrihodRequest): Long {
        // Преобразуем запрос в JSON строку
        val jsonString = objectMapper.writeValueAsString(request)

        log.info("=== ВЫЗОВ ПРОЦЕДУРЫ CREATE_PRIHORD_BY_JSON ===")
        log.info("JSON: {}", jsonString)

        //Проверяем заголовок
        val head = request.head.firstOrNull()
            ?: throw IllegalArgumentException("Отсутствует заголовок документа")

        //Проверяем склад получатель
        if (!storeRepository.existsByRn(head.storein)) {
            throw IllegalArgumentException("Для заголовка склад с RN=${head.storein} не найден")
        }

        //Проверяем Спецификацию
        val spec = request.spec.firstOrNull()
            ?: throw IllegalArgumentException("Спецификация отсутствут")

        //Проверяем склад получатель спецификации
        if (!storeRepository.existsByRn(spec.storein)) {
            throw IllegalArgumentException("Для номенклатуры ${spec.nomen} склад с RN=${spec.storein} не найден")
        }

        //Проверяем поставщика
        if (!agnlistRepository.existsByAgncode(head.provider)) {
            throw IllegalArgumentException("Поставщик с кодом '${head.provider}' не найден")
        }

        //Проверяем тип документа
        if (!typedocRepository.existsByDoccode(head.basisdoctype)) {
            throw IllegalArgumentException("Документ с кодом '${head.basisdoctype}' не найден")
        }

        // Вызываем процедуру
        val storedProcedure = entityManager.createStoredProcedureQuery("QREAL.CREATE_PRIHORD_BY_JSON")

        storedProcedure.registerStoredProcedureParameter("rn_", Long::class.java, ParameterMode.OUT)
        storedProcedure.registerStoredProcedureParameter("json_", String::class.java, ParameterMode.IN)
        storedProcedure.registerStoredProcedureParameter("error_", String::class.java, ParameterMode.OUT)

        storedProcedure.setParameter("json_", jsonString)

        storedProcedure.execute()

        val rn = storedProcedure.getOutputParameterValue("rn_") as? Long
        val error = storedProcedure.getOutputParameterValue("error_") as? String

        log.info("=== РЕЗУЛЬТАТ ВЫЗОВА ===")
        log.info("rn_ = {}", rn)
        log.info("error_ = {}", error)

        if (!error.isNullOrBlank()) {
            throw RuntimeException("Ошибка создания приходного ордера: $error")
        }

        if (rn == null || rn == 0L) {
            throw RuntimeException("Не удалось получить RN созданного документа")
        }

        return rn
    }
}