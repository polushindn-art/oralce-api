package com.example.oracleapi.repository.agnphonenumberlist

import com.example.oracleapi.dto.agnphonenumberlist.PhoneCardDto
import com.example.oracleapi.dto.agnphonenumberlist.PhoneCardProjection
import com.example.oracleapi.entity.table.Agnphonenumberlist
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface AgnphonenumberlistRepository : JpaRepository<Agnphonenumberlist, Long> {

    fun findByPhoneTail(phoneTail: String): List<Agnphonenumberlist>

    fun existsByPhoneTail(phoneTail: String): Boolean

    @Query("""
       SELECT 
        al.phonenumber AS phonenumber,
        ag.dscbarnumb AS dscbarnumb,
        agl.agnname AS agnname
    FROM Agnphonenumberlist al
    JOIN Agndsccard ag ON ag.phonenumberrn = al.rn
    LEFT JOIN AgnList agl ON agl.rn = al.prnagn
    WHERE al.phoneTail = :phoneTail
    """)
    fun findPhoneAndCardByPhoneTail(@Param("phoneTail") phoneTail: String): List<PhoneCardProjection>

}