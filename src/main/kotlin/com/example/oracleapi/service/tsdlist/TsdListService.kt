package com.example.oracleapi.service.tsdlist


import com.example.oracleapi.dto.JsonResponseView
import com.example.oracleapi.dto.tsdlist.Registeredjson
import com.example.oracleapi.dto.tsdlist.UsedJson
import com.example.oracleapi.repository.pbe.PbeRepository
import com.example.oracleapi.repository.user.TsdUsedRepository
import org.springframework.stereotype.Service

@Service
class TsdListService(
    private val getRegisteredSessionsProcedure: TsdListGetRegistered,
    private val getTsdUsedView: TsdUsedRepository,
    private val pbeRepository: PbeRepository
) {

    fun getRegisteredSessions(sn: String?): JsonResponseView<Registeredjson> {
        return getRegisteredSessionsProcedure.execute(sn)
    }

    fun getUsedTsd(pbe: Long?): JsonResponseView<UsedJson> {
        val startTime = System.currentTimeMillis()

        // Валидация
        if (pbe == null) {
            throw IllegalArgumentException("Параметр 'pbe' не может быть null")
        }

        if (!pbeRepository.existsById(pbe)) {
            throw IllegalArgumentException("Подразделение с rn = $pbe не существует")
        }

        val data = getTsdUsedView.findTsdUsed(pbe)

        return JsonResponseView(
            data.size,
            System.currentTimeMillis() - startTime,
            data
        )
    }

    /**
     * Получить информацию о пользователе по SN терминала
     * @return UserInfo или null, если терминал не активен
     */
    fun getUserByTerminalSn(sn: String): UserInfo? {
        val result = getRegisteredSessionsProcedure.execute(sn)
        // Берем первую активную сессию для этого терминала
        return result.row.firstOrNull()?.let { session ->
                        UserInfo(
                            usercode = session.usercode ?: return null,
                            username = session.agnname ?: session.usercode,
                            pin = session.pin
                        )
                    }

    }

    data class UserInfo(
        val usercode: String,
        val username: String,
        val pin: Long?
    )

}
