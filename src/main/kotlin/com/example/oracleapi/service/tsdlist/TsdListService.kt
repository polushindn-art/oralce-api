package com.example.oracleapi.service.tsdlist

import com.example.oracleapi.common.ProcedureResult
import com.example.oracleapi.dto.userlist.RegisteredJsonResponse
import org.springframework.stereotype.Service

@Service
class TsdListService(private val getRegisteredSessionsProcedure: TsdListGetRegistered) {

    fun getRegisteredSessions(sn: String?): ProcedureResult<RegisteredJsonResponse> {
        return getRegisteredSessionsProcedure.execute(sn)
    }

    /**
     * Получить информацию о пользователе по SN терминала
     * @return UserInfo или null, если терминал не активен
     */
    fun getUserByTerminalSn(sn: String): UserInfo? {
        return try {
            when (val result = getRegisteredSessionsProcedure.execute(sn)) {
                is ProcedureResult.Success -> {
                    // Берем первую активную сессию для этого терминала
                    result.data.sessions.firstOrNull()?.let { session ->
                        UserInfo(
                            usercode = session.usercode ?: return null,
                            username = session.agnname ?: session.usercode,
                            pin = session.pin
                        )
                    }
                }
                is ProcedureResult.Error -> null
            }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Проверить, зарегистрирован ли терминал
     */
    fun isTerminalRegistered(sn: String): Boolean {
        return getUserByTerminalSn(sn) != null
    }

    data class UserInfo(
        val usercode: String,
        val username: String,
        val pin: Long?
    )

}
