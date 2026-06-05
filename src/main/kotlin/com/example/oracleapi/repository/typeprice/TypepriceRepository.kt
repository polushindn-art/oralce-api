package com.example.oracleapi.repository.typeprice

import com.example.oracleapi.entity.table.Typeprice
import org.springframework.data.jpa.repository.JpaRepository

interface TypepriceRepository : JpaRepository<Typeprice, Long> {

}