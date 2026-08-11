package com.example.oracleapi.repository.phonebook

import com.example.oracleapi.entity.table.Phonebook
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface PhonebookRepository : JpaRepository<Phonebook, Long> {
    fun findByPhoneInt(phoneInt: String): List<Phonebook>
    fun findByPhoneSot(phoneSot: String): List<Phonebook>
    fun findByPbe(pbe: String): List<Phonebook>
    @Query("SELECT p FROM Phonebook p WHERE p.phoneTail = :phoneTail")
    fun findByPhoneTail(@Param("phoneTail") phoneTail: String): List<Phonebook>
}