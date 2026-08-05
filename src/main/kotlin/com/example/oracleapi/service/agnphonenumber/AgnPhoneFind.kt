package com.example.oracleapi.service.agnphonenumber

import com.example.oracleapi.dto.agnphonenumberlist.AgnphonenumberlistDto
import com.example.oracleapi.repository.agnphonenumberlist.AgnphonenumberlistRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AgnPhoneFind(
    private val repository: AgnphonenumberlistRepository
) {
    @Transactional(readOnly = true)
    fun searchByPhone(rawPhone: String): List<AgnphonenumberlistDto> {
        // 1. Очищаем входящий номер: оставляем только цифры
        val digitsOnly = rawPhone.replace(Regex("[^0-9]"), "")

        // 2. Получаем "хвост" из 10 цифр (логика аналогична функции в БД)
        val searchTail = if (digitsOnly.length >= 10) {
            digitsOnly.takeLast(10)
        } else {
            digitsOnly
        }

        // 3. Выполняем поиск через репозиторий по индексированному полю
        val entities = repository.findByPhoneTail(searchTail)

        // 4. Превращаем список сущностей в список DTO
        return entities.map { AgnphonenumberlistDto.fromEntity(it) }
    }
}