package com.example.oracleapi.repository.tsd
import com.example.oracleapi.dto.tsdlist.RegisteredjsonResponse
import com.example.oracleapi.dto.tsdlist.UsedJsonResponse
import com.example.oracleapi.entity.table.TsdListHistory
import com.example.oracleapi.entity.table.Tsdlist
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface TsdListHistoryRepository : JpaRepository<TsdListHistory, Long> {

    /**
     * Получить все активные сессии (timeend IS NULL)
     */
    @Query(
        """
        SELECT new com.example.oracleapi.dto.tsdlist.RegisteredjsonResponse(
            t.sn,
            FUNCTION('TO_CHAR', th.timestart, 'YYYY-MM-DD HH24:MI:SS'),
            ul.usercode,
            a.agnname,
            ul.parole,
            ul.dscbarnumb,
            CAST(ul.pin AS long),
            p.pbecode,
            p.rn,
            null,
            null
        )
        FROM TsdListHistory th
        JOIN th.tsdList t
        LEFT JOIN th.userList ul
        LEFT JOIN AgnList a ON ul.useragn = a.rn
        LEFT JOIN t.pbe p
        WHERE th.timeend IS NULL
        AND t.deleted IS NULL
        AND (:sn IS NULL OR t.sn = :sn)
        ORDER BY th.timestart DESC
    """
    )
    fun findRegisteredSessions(@Param("sn") sn: String?): List<RegisteredjsonResponse>

    /**
     * Получить активных пользователей ТСД
     */
    @Query(
        """
        SELECT new com.example.oracleapi.dto.tsdlist.UsedJsonResponse(
            ul.usercode,
            a.agnname,
            t.sn,
            t.deviceid,
            FUNCTION('TO_CHAR', th.timestart, 'YYYY-MM-DD HH24:MI:SS')
        )
        FROM TsdListHistory th
        JOIN th.tsdList t
        JOIN th.userList ul
        LEFT JOIN AgnList a ON ul.useragn = a.rn
        WHERE th.timeend IS NULL
        AND t.deleted IS NULL
        AND (:pbe IS NULL OR t.pbe.rn = :pbe)
        ORDER BY a.agnname
    """
    )
    fun findActiveUsers(@Param("pbe") pbe: Long?): List<UsedJsonResponse>

    /**
     * Найти активную сессию по SN (для авторизации терминала)
     */
    @Query("SELECT th FROM TsdListHistory th " +
            "JOIN FETCH th.tsdList t " +
            "JOIN FETCH th.userList ul " +
            "WHERE t.sn = :sn " +
            "AND th.timeend IS NULL " +
            "AND t.deleted IS NULL")
    fun findActiveSessionBySn(@Param("sn") sn: String): TsdListHistory?

    /**
     * Проверить наличие активной сессии
     */
    @Query("SELECT COUNT(th) > 0 FROM TsdListHistory th " +
            "JOIN th.tsdList t " +
            "WHERE t.sn = :sn " +
            "AND th.timeend IS NULL " +
            "AND t.deleted IS NULL")
    fun existsActiveSessionBySn(@Param("sn") sn: String): Boolean

    /**
     * Найти активную сессию по Device ID (для авторизации терминала)
     */
    @Query("SELECT th FROM TsdListHistory th " +
            "JOIN FETCH th.tsdList t " +
            "JOIN FETCH th.userList ul " +
            "WHERE t.deviceid = :deviceId " +
            "AND th.timeend IS NULL " +
            "AND t.deleted IS NULL")
    fun findActiveSessionByDeviceId(@Param("deviceId") deviceId: String): TsdListHistory?

    /**
     * Проверить наличие активной сессии по Device ID
     */
    @Query("SELECT COUNT(th) > 0 FROM TsdListHistory th " +
            "JOIN th.tsdList t " +
            "WHERE t.deviceid = :deviceId " +
            "AND th.timeend IS NULL " +
            "AND t.deleted IS NULL")
    fun existsActiveSessionByDeviceId(@Param("deviceId") deviceId: String): Boolean

    /**
     * Найти терминал по Device ID (без проверки сессии)
     */
    @Query("SELECT t FROM Tsdlist t " +
            "WHERE t.deviceid = :deviceId " +
            "AND t.deleted IS NULL")
    fun findTerminalByDeviceId(@Param("deviceId") deviceId: String): Tsdlist?
}