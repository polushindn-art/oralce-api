package com.example.oracleapi.service.max

import com.example.oracleapi.repository.maxUserAgn.MaxUserAgnRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

@Service
class AvatarService(
    private val restTemplate: RestTemplate,
    private val maxUserAgnRepository: MaxUserAgnRepository
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * Скачать и сохранить аватар в БД
     */
    @Async
    @Transactional
    fun downloadAndSaveAvatar(chatId: String, avatarUrl: String) {
        try {
            log.info("📥 Скачивание аватара для chatId=$chatId")

            val avatarBytes = restTemplate.getForObject<ByteArray>(avatarUrl)
            val userAgn = maxUserAgnRepository.findByChatId(chatId).firstOrNull()
            userAgn?.let {
                it.avatar = avatarBytes
                maxUserAgnRepository.save(it)
                log.info("✅ Аватар сохранён для chatId=$chatId (${avatarBytes.size} байт)")
            }
        } catch (e: Exception) {
            log.error("❌ Ошибка скачивания аватара для chatId=$chatId", e)
        }
    }
}