package com.example.oracleapi.service.user

import com.example.oracleapi.dto.user.UserList
import com.example.oracleapi.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.sql.SQLException
import java.util.*
import javax.sql.DataSource

private val log = LoggerFactory.getLogger(CustomUserDetailSrv::class.java)

@Service
class CustomUserDetailSrv(private val employeeRepository: UserRepository): UserDetailsService {

    fun getAllUser(): List<UserList> {
        val res = employeeRepository.findAll()
        return res
    }

    fun getOneUserByUserCode(usercode: String): Optional<UserList> {
        return employeeRepository.findByusercode(usercode)
    }

    override fun loadUserByUsername(username: String): UserDetails {
        log.info("loadUserByUsername для пользователя: $username")
        try {
            val user = employeeRepository.findByusercode(username.uppercase())
                .orElseThrow {
                    val text = "Пользователь с указанным именем ($username) не найден"
                    log.info("Пользователь с именем $username не найден")
                    UsernameNotFoundException(text)
                }
            log.info("Пользователь найден: ${user.usercode}")
            return User(user.usercode, user.parole, listOf())

        } catch (e: SQLException) {
            log.error("Ошибка SQL при загрузке пользователя $username", e)
            throw AuthenticationServiceException("Ошибка доступа к данным пользователя")
        } catch (e: Exception) {
            log.error("Неожиданная ошибка при загрузке пользователя $username", e)
            throw AuthenticationServiceException("Внутренняя ошибка сервиса аутентификации")
        }
    }

}