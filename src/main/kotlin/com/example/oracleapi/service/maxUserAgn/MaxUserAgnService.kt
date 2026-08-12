package com.example.oracleapi.service.maxUserAgn

import com.example.oracleapi.dto.maxUsertAgn.MaxUserAgnDto
import com.example.oracleapi.entity.table.MaxUserAgn
import com.example.oracleapi.repository.maxUserAgn.MaxUserAgnRepository
import com.example.oracleapi.service.public.PublicProcedureService
import com.example.oracleapi.util.PhoneUtils
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime

@Service
class MaxUserAgnService(
    private val maxUserAgnRepository: MaxUserAgnRepository,
    private val publicProcedureService: PublicProcedureService
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * Проверить, зарегистрирован ли уже этот чат
     */
    fun isChatRegistered(chatId: String): Boolean {
        return maxUserAgnRepository.existsByChatId(chatId)
    }

    /**
     * Найти привязку по chat_id
     */
    fun findByChatId(chatId: String): MaxUserAgnDto? {
        return MaxUserAgnDto.fromEntity(
            maxUserAgnRepository.findByChatId(chatId).firstOrNull() ?: return null,
        )
    }

    /**
     * Добавить новую привязку пользователя к номеру
     */
    @Transactional
    fun addUserAgn(
        userId: String,
        chatId: String,
        phone: String,
        botType: String = "MAIN",
        userName: String? = null
    ): MaxUserAgn {
        val phoneTail = PhoneUtils.getPhoneTail(phone)

        // Проверяем, существует ли уже привязка
        val existing = maxUserAgnRepository.findByPhoneTailAndBotType(phoneTail, botType)
        if (existing.isNotEmpty()) {
            log.warn("⚠️ Привязка уже существует: phone=$phone, botType=$botType")
            return existing.first()
        }

        // Генерируем RN
        val genIdResponse = publicProcedureService.getIdRn()
        val rn = genIdResponse.rn ?: throw IllegalStateException("Не удалось сгенерировать RN")

        val entity = MaxUserAgn().apply {
            this.rn = rn
            this.userId = userId
            this.chatId = chatId
            this.userName = userName
            this.phone = phone
            this.botType = botType
            this.createdAt = LocalDateTime.now()
            this.updatedAt = LocalDateTime.now()
            this.isActive = true
        }

        val saved = maxUserAgnRepository.save(entity)
        log.info("✅ Добавлена привязка: userId=$userId, phone=$phone, botType=$botType, chatId=$chatId")
        return saved
    }

    /**
     * Найти chat_id по номеру и типу бота
     */
    fun findChatIdByPhoneAndBotType(phone: String, botType: String): String? {
        val phoneTail = PhoneUtils.getPhoneTail(phone)
        return maxUserAgnRepository.findChatIdByPhoneAndBotType(phoneTail, botType)
    }

    /**
     * Удалить все привязки по chat_id (отписка)
     */
    @Transactional
    fun deleteByChatId(chatId: String) {
        maxUserAgnRepository.deleteByChatId(chatId)
        log.info("🗑️ Удалены все привязки для chatId=$chatId")
    }

    /**
     * Удалить все привязки пользователя
     */
    @Transactional
    fun deleteByUserId(userId: String) {
        maxUserAgnRepository.deleteByUserId(userId)
        log.info("🗑️ Удалены все привязки для userId=$userId")
    }

    /**
     * Удалить привязку по номеру и типу бота
     */
    @Transactional
    fun deleteByPhoneAndBotType(phone: String, botType: String) {
        val phoneTail = PhoneUtils.getPhoneTail(phone)
        maxUserAgnRepository.deleteByPhoneAndBotType(phoneTail, botType)
        log.info("🗑️ Удалена привязка: phone=$phone, botType=$botType")
    }

    /**
     * Проверить, существует ли привязка
     */
    fun existsByPhoneAndBotType(phone: String, botType: String): Boolean {
        val phoneTail = PhoneUtils.getPhoneTail(phone)
        return maxUserAgnRepository.existsByPhoneAndBotType(phoneTail, botType)
    }

    fun findByPhoneTailAndBotType(phoneTail: String, botType: String): MaxUserAgn? {
        return maxUserAgnRepository.findByPhoneTailAndBotType(phoneTail, botType).firstOrNull()
    }

    /**
     * Найти пользователя в MAIN боте по номеру телефона
     */
    fun findMainUserByPhone(phone: String): MaxUserAgnDto? {
        val cleanPhone = phone.replace(Regex("[^\\d+]"), "")
        val phoneTail = PhoneUtils.getPhoneTail(cleanPhone)

        val userAgn = maxUserAgnRepository.findByPhoneTailAndBotType(phoneTail, "MAIN")
            .firstOrNull { true }

        return userAgn?.let { MaxUserAgnDto.fromEntity(it) }
    }

    /**
     * Активировать пользователя по chat_id (если запись существует)
     */
    @Transactional
    fun activateByChatId(chatId: String) {
        val userAgn = maxUserAgnRepository.findByChatId(chatId).firstOrNull()
        if (userAgn != null) {
            userAgn.isActive = true
            userAgn.updatedAt = LocalDateTime.now()
            maxUserAgnRepository.save(userAgn)
            log.info("✅ Пользователь $chatId активирован")
        } else {
            log.debug("ℹ️ Пользователь $chatId не найден в БД (возможно, первый вход)")
        }
    }

    /**
     * Деактивировать пользователя по chat_id (если запись существует)
     */
    @Transactional
    fun deactivateByChatId(chatId: String) {
        val userAgn = maxUserAgnRepository.findByChatId(chatId).firstOrNull()
        if (userAgn != null) {
            userAgn.isActive = false
            userAgn.updatedAt = LocalDateTime.now()
            maxUserAgnRepository.save(userAgn)
            log.info("🗑️ Пользователь $chatId деактивирован (isActive = false)")
        } else {
            log.debug("ℹ️ Пользователь $chatId не найден в БД")
        }
    }

}