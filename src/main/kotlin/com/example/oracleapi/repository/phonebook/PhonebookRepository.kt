package com.example.oracleapi.repository.phonebook

import com.example.oracleapi.entity.table.Phonebook
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PhonebookRepository : JpaRepository<Phonebook, Long> {

    fun findByPhoneInt(phoneInt: String): Phonebook?
}