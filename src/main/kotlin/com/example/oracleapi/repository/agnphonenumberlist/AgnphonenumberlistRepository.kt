package com.example.oracleapi.repository.agnphonenumberlist

import com.example.oracleapi.entity.table.Agnphonenumberlist
import org.springframework.data.jpa.repository.JpaRepository

interface AgnphonenumberlistRepository : JpaRepository<Agnphonenumberlist, Long> {
    fun findByPhoneTail(phoneTail: String): List<Agnphonenumberlist>
}