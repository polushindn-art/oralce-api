package com.example.oracleapi.service.userlist

import com.example.oracleapi.repository.userlist.UserlistRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomUserDetailsService(
    private val userlistRepository: UserlistRepository
): UserDetailsService  {

    @Transactional(readOnly = true)
    override fun loadUserByUsername(username: String): UserDetails {
        val userlist = userlistRepository.findByUsercodeIgnoreCase(username)
            ?: throw UsernameNotFoundException("Пользователь не найден: $username")

        // Проверяем, включен ли логин для пользователя
        if (userlist.loginEnabled != 1) {
            throw UsernameNotFoundException("Аккаунт отключен: $username")
        }

        return User.builder()
            .username(userlist.usercode!!)
            .password(userlist.parole ?: "")
            .disabled(userlist.loginEnabled != 1)
            .build()
    }
}