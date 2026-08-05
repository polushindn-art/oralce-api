package com.example.oracleapi.service.notification

import com.example.oracleapi.entity.table.Phonebook
import com.example.oracleapi.repository.max.MaxUserRepository
import com.example.oracleapi.repository.phonebook.PhonebookRepository
import com.example.oracleapi.service.max.call.MessageService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class BirthdayNotificationService(
    private val phonebookRepository: PhonebookRepository,
    private val maxUserRepository: MaxUserRepository,
    private val messageService: MessageService
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * Каждый день в 9:30 проверяем дни рождения
     */
    @Scheduled(cron = "0 30 9 * * *")
    //@Scheduled(cron = "0 */1 * * * *")
    fun sendBirthdayNotifications() {
        log.info("🎂 Проверка дней рождения...")

        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd.MM")

        // Ищем всех сотрудников, у кого сегодня день рождения
        val allEmployees = phonebookRepository.findAll()
        val birthdayPeople = allEmployees.filter { employee ->
            employee.rdate?.let { birthDate ->
                birthDate.month == today.month && birthDate.dayOfMonth == today.dayOfMonth
            } ?: false
        }

        if (birthdayPeople.isEmpty()) {
            log.info("🎂 Сегодня дней рождения нет")
            return
        }

        log.info("🎂 Найдено именинников: ${birthdayPeople.size}")

        // Для каждого именинника отправляем поздравление
        birthdayPeople.forEach { employee ->
            val fullName = listOfNotNull(employee.fname, employee.nname).joinToString(" ")
            val age = employee.rdate?.let { today.year - it.year }

            // Ищем пользователей, кому отправить поздравление
            // Отправляем всем зарегистрированным в MAX
            val allUsers = maxUserRepository.findAll()
            val message = buildBirthdayMessage(fullName, age, employee)

            allUsers.forEach { user ->
                try {
                    messageService.sendMessageByUserId(
                        user.userId,
                        message,
                        "markdown"
                    )
                    log.info("✅ Поздравление отправлено пользователю ${user.userName}")
                } catch (e: Exception) {
                    log.error("❌ Не удалось отправить поздравление пользователю ${user.userName}", e)
                }
            }
        }
    }

    private fun buildBirthdayMessage(name: String, age: Int?, employee: Phonebook): String {

        val fullName = listOfNotNull(employee.fname, employee.nname).joinToString(" ")

        return """
            🎉 *Сегодня день рождения!* 🎉

            🥳 Поздравляем $fullName

            📋 Должность: ${employee.dolgnost ?: "—"}
            🏢 Отдел: ${employee.otdel ?: "—"}

            Пожелаем удачи, здоровья и счастья! 🎂🎁
        """.trimIndent()
    }
}