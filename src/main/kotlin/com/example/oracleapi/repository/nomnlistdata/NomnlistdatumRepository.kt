package com.example.oracleapi.repository.nomnlistdata

import com.example.oracleapi.entity.table.Nomnlistdata
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface NomnlistdataRepository : JpaRepository<Nomnlistdata, Long> {
    // ========== NATIVE QUERIES для SELECT ==========

    @Query(value = "SELECT * FROM NOMNLISTDATA WHERE NOMEN = :nomen", nativeQuery = true)
    fun findByNomenNative(@Param("nomen") nomen: Long): List<Nomnlistdata>

    @Query(value = "SELECT * FROM NOMNLISTDATA WHERE NOMEN = :nomen AND PHOTONUM = :photonum", nativeQuery = true)
    fun findByNomenAndPhotonumNative(@Param("nomen") nomen: Long, @Param("photonum") photonum: Int): Nomnlistdata?

    @Query(value = "SELECT * FROM NOMNLISTDATA WHERE RN = :rn", nativeQuery = true)
    fun findByRnNative(@Param("rn") rn: Long): Nomnlistdata?

    @Query(value = "SELECT COUNT(*) FROM NOMNLISTDATA WHERE NOMEN = :nomen AND DELETED IS NULL", nativeQuery = true)
    fun countByNomenNative(@Param("nomen") nomen: Long): Long

    // ========== NATIVE QUERY для INSERT ==========

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO NOMNLISTDATA (RN, NOMEN, "DATA", PHOTONUM, NEEDDOWNLOAD, MANAGER, CREATED, DELETED, DELUSER, MD5, MINIDATA, HEIGHT, WIDTH, "SIZE")
        VALUES (:rn, :nomen, :data, :photonum, :needdownload, :manager, :created, :deleted, :deluser, :md5, :minidata, :height, :width, :size)
    """, nativeQuery = true)
    fun insertNative(
        @Param("rn") rn: Long?,
        @Param("nomen") nomen: Long,
        @Param("data") data: ByteArray,
        @Param("photonum") photonum: Int,
        @Param("needdownload") needdownload: Int?,
        @Param("manager") manager: Long?,
        @Param("created") created: LocalDateTime?,
        @Param("deleted") deleted: LocalDateTime?,
        @Param("deluser") deluser: Long?,
        @Param("md5") md5: ByteArray?,
        @Param("minidata") minidata: ByteArray?,
        @Param("height") height: Int?,
        @Param("width") width: Int?,
        @Param("size") size: Long?
    ): Int

    // ========== NATIVE QUERY для UPDATE ==========

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE NOMNLISTDATA SET 
            NOMEN = :nomen,
            "DATA" = :data,
            PHOTONUM = :photonum,
            NEEDDOWNLOAD = :needdownload,
            MANAGER = :manager,
            CREATED = :created,
            DELETED = :deleted,
            DELUSER = :deluser,
            MD5 = :md5,
            MINIDATA = :minidata,
            HEIGHT = :height,
            WIDTH = :width,
            "SIZE" = :size
        WHERE RN = :rn
    """, nativeQuery = true)
    fun updateNative(
        @Param("rn") rn: Long,
        @Param("nomen") nomen: Long,
        @Param("data") data: ByteArray,
        @Param("photonum") photonum: Int,
        @Param("needdownload") needdownload: Int?,
        @Param("manager") manager: Long?,
        @Param("created") created: LocalDateTime?,
        @Param("deleted") deleted: LocalDateTime?,
        @Param("deluser") deluser: Long?,
        @Param("md5") md5: ByteArray?,
        @Param("minidata") minidata: ByteArray?,
        @Param("height") height: Int?,
        @Param("width") width: Int?,
        @Param("size") size: Long?
    ): Int

    // ========== NATIVE QUERIES для SOFT DELETE ==========

    @Modifying
    @Transactional
    @Query(value = "UPDATE NOMNLISTDATA SET DELETED = :deleted, DELUSER = :deluser, DATA = null, PHOTONUM = null  WHERE RN = :rn", nativeQuery = true)
    fun softDeleteByRnNative(
        @Param("rn") rn: Long,
        @Param("deleted") deleted: LocalDateTime,
        @Param("deluser") deluser: Long?
    ): Int

    @Modifying
    @Transactional
    @Query(value = "UPDATE NOMNLISTDATA SET DELETED = :deleted, DELUSER = :deluser, DATA = null, PHOTONUM = null  WHERE NOMEN = :nomen and deleted is null", nativeQuery = true)
    fun softDeleteByNomenNative(
        @Param("nomen") nomen: Long,
        @Param("deleted") deleted: LocalDateTime,
        @Param("deluser") deluser: Long?
    ): Int

    // ========== NATIVE QUERY для HARD DELETE ==========

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM NOMNLISTDATA WHERE RN = :rn", nativeQuery = true)
    fun hardDeleteByRnNative(@Param("rn") rn: Long): Int

    // Найти максимальный номер фото для номенклатуры
    @Query(value = "SELECT COALESCE(MAX(PHOTONUM),0) FROM NOMNLISTDATA WHERE NOMEN = :nomen", nativeQuery = true)
    fun findMaxPhotonumByNomenNative(@Param("nomen") nomen: Long): Int?

}