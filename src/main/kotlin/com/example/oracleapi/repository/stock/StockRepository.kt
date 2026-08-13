package com.example.oracleapi.repository.stock

import com.example.oracleapi.dto.stock.StockInfoProjection
import com.example.oracleapi.entity.table.Stock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.math.BigDecimal

interface StockRepository : JpaRepository<Stock, Long> {
    fun findStocksByNomen(nomen: BigDecimal): List<Stock>

    @Query("select t from Stock t where t.nomen = :nomen and t.quanttosale > 0")
    fun findStocksByNomenQuantNotNull(nomen: BigDecimal): List<Stock>

        @Query(
            value = """
            SELECT 
                n.NOMENNAME as nomenName,
                st.STORECODE as storeCode,
                s.NOMEN as nomenId,
                s.STORE as storeId,
                s.QUANTTOSALE as quantToSale,
                st.STOREPBE as storePbe,
                p.PRICE as price,
                QREAL.PKG_PRICE.GETPRICEOUT(s.NOMEN, s.STORE, 1) as priceCard,
                pb.ADDRESS,
                pb.PBECODE,
                st.webstore
            FROM QREAL.stock s
            LEFT JOIN QREAL.store st ON st.RN = s.STORE
            LEFT JOIN QREAL.price p ON p.NOMEN = s.NOMEN AND p.PBE = st.STOREPBE
            LEFT JOIN QREAL.pbe pb ON pb.RN = p.PBE
            LEFT JOIN QREAL.nomnlist n ON n.RN = s.NOMEN
            WHERE s.NOMEN = :nomen
        """,
            nativeQuery = true
        )
        fun findStockInfo(@Param("nomen") nomen: BigDecimal): List<StockInfoProjection>


}