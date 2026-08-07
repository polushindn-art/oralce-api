package com.example.oracleapi.dto.orderspec

import com.example.oracleapi.entity.table.Orderspec
import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.LocalDate

data class OrderSpecResponse(
    // ===== 1. rn_ in out =====
    val rn: Long,

    // ===== 2. prn_ in =====
    val prn: Long,

    // ===== 3. nomen_ in =====
    val nomen: Long,

    // ===== 4. quant_ in =====
    val quant: BigDecimal,

    // ===== 5. summ_ in =====
    val summ: BigDecimal,

    // ===== 6. factquant_ in =====
    val factquant: BigDecimal,

    // ===== 7. quantbreak_ in =====
    val quantbreak: BigDecimal,

    // ===== 8. notconquant_ in =====
    val notconquant: BigDecimal?,

    // ===== 9. undefinedquant_ in =====
    val undefinedquant: BigDecimal?,

    // ===== 10. prquant_ in =====
    val prquant: BigDecimal,

    // ===== 11. prsum_ in =====
    val prsum: BigDecimal,

    // ===== 12. ndsrate_ in =====
    val ndsrate: BigDecimal,

    // ===== 13. country_ in =====
    val country: Long?,

    // ===== 14. gtd_ in =====
    val gtd: String?,

    // ===== 15. pdpricecs_ in =====
    val pdpricecs: BigDecimal,

    // ===== 16. pdprice1_ in =====
    val pdprice1: BigDecimal,

    // ===== 17. pdprice2_ in =====
    val pdprice2: BigDecimal,

    // ===== 18. pdprice3_ in =====
    val pdprice3: BigDecimal,

    // ===== 19. pdprice4_ in =====
    val pdprice4: BigDecimal,

    // ===== 20. pdprice5_ in =====
    val pdprice5: BigDecimal,

    // ===== 21. pdnomncatcs_ in =====
    val pdnomncatcs: Long?,

    // ===== 22. pdnomncat1_ in =====
    val pdnomncat1: Long?,

    // ===== 23. pdnomncat2_ in =====
    val pdnomncat2: Long?,

    // ===== 24. pdnomncat3_ in =====
    val pdnomncat3: Long?,

    // ===== 25. pdnomncat4_ in =====
    val pdnomncat4: Long?,

    // ===== 26. pdnomncat5_ in =====
    val pdnomncat5: Long?,

    // ===== 27. notelogist_ in =====
    val notelogist: String?,

    // ===== 28. whsconst_ in =====
    val whsconst: Long,

    // ===== 29. check_rozn_price_ in =====
    val checkRoznPrice: String,

    // ===== 30. storein_ in =====
    val storein: Long?,

    // ===== 31. ChangeOverHead in =====
    val changeOverHead: Int,

    // ===== 32. DlyaKompl_ in =====
    val dlyaKompl: Int?,

    // ===== 33. KomplRn_ in =====
    val komplRn: Long?,

    // ===== 34. KomplQty_ in =====
    val komplQty: Int?,

    // ===== 35. QtyVKompl_ in =====
    val qtyVKompl: BigDecimal?,

    // ===== 36. CalcQtyPost_ in =====
    val calcQtyPost: BigDecimal?,

    // ===== 37. DocQtyPost_ in =====
    val docQtyPost: BigDecimal?,

    // ===== 38. rnDEI_ in =====
    val rnDEI: Long?,

    // ===== 39. FactQtyPost_ in =====
    val factQtyPost: BigDecimal?,

    // ===== 40. DATE_PRODUCTION_ in =====
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    val dateProduction: LocalDate?,

    // ===== 41. QUANTDOC_ in =====
    val quantDoc: BigDecimal?,

    // ===== 42. SUMMDOC_ in =====
    val summdoc: BigDecimal?
) {
    companion object {
        fun fromEntity(entity: Orderspec): OrderSpecResponse {
            return OrderSpecResponse(
                // 1. rn
                rn = entity.rn ?: 0,
                // 2. prn
                prn = entity.prn ?: 0,
                // 3. nomen
                nomen = entity.nomen ?: 0,
                // 4. quant
                quant = entity.quant ?: BigDecimal.ZERO,
                // 5. summ
                summ = entity.summ ?: BigDecimal.ZERO,
                // 6. factquant
                factquant = entity.factquant ?: BigDecimal.ZERO,
                // 7. quantbreak
                quantbreak = entity.quantbreak ?: BigDecimal.ZERO,
                // 8. notconquant
                notconquant = entity.notconquant,
                // 9. undefinedquant
                undefinedquant = entity.undefinedquant,
                // 10. prquant
                prquant = entity.prquant ?: BigDecimal.ZERO,
                // 11. prsum
                prsum = entity.prsum ?: BigDecimal.ZERO,
                // 12. ndsrate
                ndsrate = entity.ndsrate ?: BigDecimal.ZERO,
                // 13. country
                country = entity.country ?: 0,
                // 14. gtd
                gtd = entity.gtd,
                // 15. pdpricecs
                pdpricecs = entity.pdpricecs ?: BigDecimal.ZERO,
                // 16. pdprice1
                pdprice1 = entity.pdprice1 ?: BigDecimal.ZERO,
                // 17. pdprice2
                pdprice2 = entity.pdprice2 ?: BigDecimal.ZERO,
                // 18. pdprice3
                pdprice3 = entity.pdprice3 ?: BigDecimal.ZERO,
                // 19. pdprice4
                pdprice4 = entity.pdprice4 ?: BigDecimal.ZERO,
                // 20. pdprice5
                pdprice5 = entity.pdprice5 ?: BigDecimal.ZERO,
                // 21. pdnomncatcs
                pdnomncatcs = entity.pdnomncatcs,
                // 22. pdnomncat1
                pdnomncat1 = entity.pdnomncat1,
                // 23. pdnomncat2
                pdnomncat2 = entity.pdnomncat2,
                // 24. pdnomncat3
                pdnomncat3 = entity.pdnomncat3,
                // 25. pdnomncat4
                pdnomncat4 = entity.pdnomncat4,
                // 26. pdnomncat5
                pdnomncat5 = entity.pdnomncat5,
                // 27. notelogist
                notelogist = entity.notelogist,
                // 28. whsconst
                whsconst = entity.whsconst ?: 1,
                // 29. check_rozn_price
                checkRoznPrice = entity.checkRoznPrice ?: "111111",
                // 30. storein
                storein = entity.storein,
                // 31. ChangeOverHead
                changeOverHead = 0,
                // 32. DlyaKompl
                dlyaKompl = entity.dlyakompl,
                // 33. KomplRn
                komplRn = entity.komplrn,
                // 34. KomplQty
                komplQty = entity.komplqty,
                // 35. QtyVKompl
                qtyVKompl = entity.qtyvkompl,
                // 36. CalcQtyPost
                calcQtyPost = entity.calcqtypost,
                // 37. DocQtyPost
                docQtyPost = entity.docqtypost,
                // 38. rnDEI
                rnDEI = entity.rndei,
                // 39. FactQtyPost
                factQtyPost = entity.factqtypost,
                // 40. DATE_PRODUCTION
                dateProduction = entity.dateProduction,
                // 41. QUANTDOC
                quantDoc = entity.quantdoc,
                // 42. SUMMDOC
                summdoc = entity.summdoc
            )
        }
    }
}