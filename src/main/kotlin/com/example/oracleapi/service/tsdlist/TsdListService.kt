package com.example.oracleapi.service.tsdlist

import com.example.oracleapi.Helper
import com.example.oracleapi.dto.store.StoreSimpleResponse
import com.example.oracleapi.dto.tsdlist.*
import com.example.oracleapi.dto.tsdparam.ParamDto
import com.example.oracleapi.dto.userpart.PartInfo
import com.example.oracleapi.entity.Tsdlist
import com.example.oracleapi.repository.agnlist.AgnListRepository
import com.example.oracleapi.repository.pbe.PbeRepository
import com.example.oracleapi.repository.tsd.TsdListHistoryRepository
import com.example.oracleapi.repository.tsd.TsdListRepository
import com.example.oracleapi.repository.userpart.UserpartRepository
import com.example.oracleapi.service.StoreService
import com.example.oracleapi.service.public.PublicProcedureService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class TsdListService(
    private val tsdHistoryRepository: TsdListHistoryRepository,
    private val pbeRepository: PbeRepository,
    private val agnListRepository: AgnListRepository,
    private val storeService: StoreService,
    private val tsdlistRepository: TsdListRepository,
    private val publicProcedureService: PublicProcedureService,
    private val userpartRepository: UserpartRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = true)
    fun getRegisteredSessions(sn: String?): List<Registeredjson> {
        val data = tsdHistoryRepository.findRegisteredSessions(sn)

        // Догружаем склады для каждого PBE
        return data.map { session ->
            val stores = if (session.pbern != null) {
                storeService.getStoresByPbeAndNote(session.pbern, "#ТСД")
            } else {
                emptyList()
            }

            val params = if (session.sn != null) {
                val terminal = tsdlistRepository.findTerminalWithParams(session.sn)
                terminal?.params?.map { param ->
                    ParamDto(
                        name = param.paramname,
                        value = param.paramvalue,
                        description = param.description
                    )
                } ?: emptyList()
            } else {
                emptyList()
            }

            session.copy(
                store = stores,
                param = params
            )
        }
    }

    @Transactional(readOnly = true)
    fun getUsedTsd(pbe: Long?): List<UsedJson> {
        // Валидация PBE если передан
        if (pbe != null && !pbeRepository.existsById(pbe)) {
            throw IllegalArgumentException("Подразделение с rn = $pbe не существует")
        }

        return tsdHistoryRepository.findActiveUsers(pbe)
    }

    /**
     * Получить информацию о пользователе по Device ID терминала (для авторизации)
     */
    @Transactional(readOnly = true)
    fun getUserByTerminalDeviceId(deviceId: String): UserInfo? {
        log.debug("Looking for active session by Device ID: {}", deviceId)

        val activeSession = tsdHistoryRepository.findActiveSessionByDeviceId(deviceId)

        return activeSession?.let { history ->
            val userList = history.userList

            // Проверяем что usercode не null и не пустой
            val usercode = userList?.usercode
            if (usercode.isNullOrBlank()) {
                log.warn("UserList has no usercode for Device ID: {}", deviceId)
                return null
            }

            // Получаем имя из AGNLIST
            val agnName = userList.useragn?.let { agnListRepository.findById(it).orElse(null)?.agnname }

            // Получаем ВСЕ роли пользователя
            val userRn = userList.rn
            val parts = userRn?.let { getPartsByUserRn(it) } ?: emptyList()

            UserInfo(
                rn = userList.rn ?: 0L,
                usercode = usercode,
                username = agnName ?: usercode,
                pin = userList.pin?.toLong(),
                parole = userList.parole ?: "",
                userAgn = userList.useragn ?: 0L,
                dscbarnumb = userList.dscbarnumb ?: "",
                parts = parts
            )
        }
    }

    /**
     * Проверка активности терминала по Device ID
     */
    @Transactional(readOnly = true)
    fun isTerminalActiveByDeviceId(deviceId: String): Boolean {
        return tsdHistoryRepository.existsActiveSessionByDeviceId(deviceId)
    }

    /**
     * Получить информацию о терминале по Device ID
     */
    @Transactional(readOnly = true)
    fun getTerminalByDeviceId(deviceId: String): Tsdlist? {
        return tsdHistoryRepository.findTerminalByDeviceId(deviceId)
    }

    /**
     * Получить полную информацию о терминале в формате Registeredjson
     */
    @Transactional(readOnly = true)
    fun getTerminalFullInfoAsRegisteredjson(deviceId: String): Registeredjson? {
        log.debug("Getting full info for Device ID: {}", deviceId)

        // 1. Находим активную сессию
        val activeSession = tsdHistoryRepository.findActiveSessionByDeviceId(deviceId)
        if (activeSession == null) {
            log.warn("No active session for Device ID: {}", deviceId)
            return null
        }

        val tsdList = activeSession.tsdList
        val userList = activeSession.userList

        // 2. Получаем имя агента
        val agnName = userList?.useragn?.let { agnListRepository.findById(it).orElse(null)?.agnname }

        // 3. Получаем PBE информацию
        val pbe = tsdList?.pbe
        val pbecode = pbe?.pbecode
        val pbern = pbe?.rn

        // 4. Получаем склады для PBE
        val stores = if (pbern != null) {
            storeService.getStoresByPbeAndNote(pbern, "#ТСД").map { store ->
                StoreSimpleResponse(
                    rn = store.rn,
                    storecode = store.storecode,
                    storename = store.storename,
                    eschema = store.eschema,
                    usesas = store.usesas,
                    note = store.note
                )
            }
        } else {
            emptyList()
        }

        // 5. Получаем параметры терминала
        val params = if (tsdList?.sn != null) {
            val terminalWithParams = tsdlistRepository.findTerminalWithParams(tsdList.sn!!)
            terminalWithParams?.params?.map { param ->
                ParamDto(
                    name = param.paramname,
                    value = param.paramvalue,
                    description = param.description
                )
            } ?: emptyList()
        } else {
            emptyList()
        }

        // 6. Формируем Registeredjson
        return Registeredjson(
            sn = tsdList?.sn,
            timestart = activeSession.timestart?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            userrn = userList?.rn,
            usercode = userList?.usercode,
            agnname = agnName,
            parole = userList?.parole,
            dscbarnumb = userList?.dscbarnumb,
            pin = userList?.pin?.toLong(),
            pbecode = pbecode,
            pbern = pbern,
            store = stores,
            param = params
        )
    }

    /**
     * Обновить существующий терминал или создать новый, если его нет
     * При создании нового терминала генерирует случайный RFID
     */
    @Transactional
    fun upsertTerminal(request: TsdUpsertRequest): TsdUpsertResponse {
        log.debug("UPSERT терминал с Device ID: {}", request.deviceId)

        // Ищем существующий терминал по Device ID
        val existingTerminal = tsdlistRepository.findByDeviceid(request.deviceId)

        return if (existingTerminal == null) {
            // Создаем новый терминал
            createNewTerminal(request)
        } else {
            // Обновляем существующий
            updateExistingTerminal(existingTerminal, request)
        }
    }

    private fun createNewTerminal(request: TsdUpsertRequest): TsdUpsertResponse {
        // Генерируем новый RN через хранимую процедуру
        val genIdResponse = publicProcedureService.getIdRn()
        val newRn = genIdResponse.rn ?: throw RuntimeException("Не удалось сгенерировать RN")

        // Генерируем случайный RFID
        val randomRfid = Helper.generateRandomRfid()

        // Если SN не передан, передаем 014.0000
        val terminalSn = request.sn ?: "014.0000"

        val terminal = Tsdlist().apply {
            rn = newRn
            sn = terminalSn
            deviceid = request.deviceId
            rfid = randomRfid
            pbe = request.pbe?.let { pbeRepository.findById(it).orElse(null) }
            note = request.note
            tsdprogram = request.tsdprogram
            curversion = request.curversion
            tsdip = request.tsdip
            tsdname = request.tsdname
            versioncode = request.versioncode ?: 0
            datestart = LocalDateTime.now()
            newversion = null
            deleted = null
            updateversion = 0
        }

        tsdlistRepository.save(terminal)
        log.info("Создан новый терминал с RN: {}, SN: {}, RFID: {}", newRn, request.sn, randomRfid)

        return TsdUpsertResponse(
            rn = terminal.rn!!,
            deviceId = request.deviceId,
            sn = terminalSn,
            operation = "INSERT",
            isNew = true,
            generatedRfid = randomRfid
        )
    }

    private fun updateExistingTerminal(
        terminal: Tsdlist,
        request: TsdUpsertRequest
    ): TsdUpsertResponse {
        terminal.apply {
            request.sn?.let { newSn ->
                if (sn != newSn) {
                    sn = newSn
                    log.debug("Обновлен SN для Device ID {}: {} -> {}", deviceid, sn, newSn)
                }
            }
            request.curversion?.let { curversion = it }
            datestart = LocalDateTime.now()
            request.tsdprogram?.let { tsdprogram = it }
            request.tsdip?.let { tsdip = it }
            request.tsdname?.let { tsdname = it }
            request.versioncode?.let { versioncode = it }
            request.note?.let { note = it }
        }

        tsdlistRepository.save(terminal)
        log.info("Обновлен терминал с RN: {}, Device ID: {}, SN: {}", terminal.rn, request.deviceId, terminal.sn)

        return TsdUpsertResponse(
            rn = terminal.rn!!,
            deviceId = request.deviceId,
            sn = terminal.sn,
            operation = "UPDATE",
            isNew = false,
            generatedRfid = terminal.rfid ?: "D0"
        )
    }

    /**
     * Получить все роли (part) для пользователя
     */
    @Transactional(readOnly = true)
    fun getPartsByUserRn(userRn: Long): List<PartInfo> {
        val userParts = userpartRepository.findByUserrnRn(userRn)
        return userParts.mapNotNull { userpart ->
            userpart.part?.let { part ->
                PartInfo(
                    rn = part.rn ?: 0,
                    partcode = part.partcode ?: "",
                    partname = part.partname ?: ""
                )
            }
        }
    }

    fun getSnByDeviceId(deviceId: String): TsdIdResponse {
        return TsdIdResponse(
            tsdlistRepository.findByDeviceid(deviceId)?.sn ?: "014.XXXX"
        )
    }

}

data class UserInfo(
    val rn: Long,
    val usercode: String,
    val username: String,
    val pin: Long?,
    val parole: String,
    val userAgn: Long,
    val dscbarnumb: String,
    val parts: List<PartInfo>
)