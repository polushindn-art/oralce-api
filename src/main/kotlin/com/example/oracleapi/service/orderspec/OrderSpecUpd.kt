package com.example.oracleapi.service.orderspec

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.orderspec.upd.OrderSpecUpdateRequest
import com.example.oracleapi.dto.orderspec.upd.OrderSpecUpdateResponse
import org.springframework.stereotype.Component
import java.sql.Types
import javax.sql.DataSource

@Component
class OrderSpecUpd(
    dataSource: DataSource
) : BasePackage(dataSource) {

    override val pkg = "PKG_ORDERSPEC"
    override val method = "UPD"
    override val count = 42

    fun take(request: OrderSpecUpdateRequest): OrderSpecUpdateResponse {

        return dataSource.executePrc { stmt ->
            // 1. rn_ in out (IN и OUT)
            stmt.setLong(1, request.rn)
            stmt.registerOutParameter(1, Types.NUMERIC)

            // 2. prn_ in
            stmt.setLong(2, request.prn)

            // 3. nomen_ in
            stmt.setLong(3, request.nomen)

            // 4. quant_ in
            stmt.setBigDecimal(4, request.quant)

            // 5. summ_ in
            stmt.setBigDecimal(5, request.summ)

            // 6. factquant_ in
            stmt.setBigDecimal(6, request.factquant)

            // 7. quantbreak_ in
            stmt.setBigDecimal(7, request.quantbreak)

            // 8. notconquant_ in
            if (request.notconquant != null) {
                stmt.setBigDecimal(8, request.notconquant)
            } else {
                stmt.setNull(8, Types.NUMERIC)
            }

            // 9. undefinedquant_ in
            if (request.undefinedquant != null) {
                stmt.setBigDecimal(9, request.undefinedquant)
            } else {
                stmt.setNull(9, Types.NUMERIC)
            }

            // 10. prquant_ in
            stmt.setBigDecimal(10, request.prquant)

            // 11. prsum_ in
            stmt.setBigDecimal(11, request.prsum)

            // 12. ndsrate_ in
            stmt.setBigDecimal(12, request.ndsrate)

            // 13. country_ in
            if (request.country != null) {
                stmt.setLong(13, request.country)
            } else {
                stmt.setNull(13, Types.NUMERIC)
            }

            // 14. gtd_ in
            if (request.gtd != null) {
                stmt.setString(14, request.gtd)
            } else {
                stmt.setNull(14, Types.VARCHAR)
            }

            // 15. pdpricecs_ in
            stmt.setBigDecimal(15, request.pdpricecs)

            // 16. pdprice1_ in
            stmt.setBigDecimal(16, request.pdprice1)

            // 17. pdprice2_ in
            stmt.setBigDecimal(17, request.pdprice2)

            // 18. pdprice3_ in
            stmt.setBigDecimal(18, request.pdprice3)

            // 19. pdprice4_ in
            stmt.setBigDecimal(19, request.pdprice4)

            // 20. pdprice5_ in
            stmt.setBigDecimal(20, request.pdprice5)

            // 21. pdnomncatcs_ in
            if (request.pdnomncatcs != null) {
                stmt.setLong(21, request.pdnomncatcs)
            } else {
                stmt.setNull(21, Types.NUMERIC)
            }

            // 22. pdnomncat1_ in
            if (request.pdnomncat1 != null) {
                stmt.setLong(22, request.pdnomncat1)
            } else {
                stmt.setNull(22, Types.NUMERIC)
            }

            // 23. pdnomncat2_ in
            if (request.pdnomncat2 != null) {
                stmt.setLong(23, request.pdnomncat2)
            } else {
                stmt.setNull(23, Types.NUMERIC)
            }

            // 24. pdnomncat3_ in
            if (request.pdnomncat3 != null) {
                stmt.setLong(24, request.pdnomncat3)
            } else {
                stmt.setNull(24, Types.NUMERIC)
            }

            // 25. pdnomncat4_ in
            if (request.pdnomncat4 != null) {
                stmt.setLong(25, request.pdnomncat4)
            } else {
                stmt.setNull(25, Types.NUMERIC)
            }

            // 26. pdnomncat5_ in
            if (request.pdnomncat5 != null) {
                stmt.setLong(26, request.pdnomncat5)
            } else {
                stmt.setNull(26, Types.NUMERIC)
            }

            // 27. notelogist_ in
            if (request.notelogist != null) {
                stmt.setString(27, request.notelogist)
            } else {
                stmt.setNull(27, Types.VARCHAR)
            }

            // 28. whsconst_ in
            stmt.setLong(28, request.whsconst)

            // 29. check_rozn_price_ in
            stmt.setString(29, request.checkRoznPrice)

            // 30. storein_ in
            if (request.storein != null) {
                stmt.setLong(30, request.storein)
            } else {
                stmt.setNull(30, Types.NUMERIC)
            }

            // 31. ChangeOverHead in
            stmt.setInt(31, request.changeOverHead)

            // 32. DlyaKompl_ in
            if (request.dlyaKompl != null) {
                stmt.setInt(32, request.dlyaKompl)
            } else {
                stmt.setNull(32, Types.NUMERIC)
            }

            // 33. KomplRn_ in
            if (request.komplRn != null) {
                stmt.setLong(33, request.komplRn)
            } else {
                stmt.setNull(33, Types.NUMERIC)
            }

            // 34. KomplQty_ in
            if (request.komplQty != null) {
                stmt.setInt(34, request.komplQty)
            } else {
                stmt.setNull(34, Types.NUMERIC)
            }

            // 35. QtyVKompl_ in
            if (request.qtyVKompl != null) {
                stmt.setBigDecimal(35, request.qtyVKompl)
            } else {
                stmt.setNull(35, Types.NUMERIC)
            }

            // 36. CalcQtyPost_ in
            if (request.calcQtyPost != null) {
                stmt.setBigDecimal(36, request.calcQtyPost)
            } else {
                stmt.setNull(36, Types.NUMERIC)
            }

            // 37. DocQtyPost_ in
            if (request.docQtyPost != null) {
                stmt.setBigDecimal(37, request.docQtyPost)
            } else {
                stmt.setNull(37, Types.NUMERIC)
            }

            // 38. rnDEI_ in
            if (request.rnDEI != null) {
                stmt.setLong(38, request.rnDEI)
            } else {
                stmt.setNull(38, Types.NUMERIC)
            }

            // 39. FactQtyPost_ in
            if (request.factQtyPost != null) {
                stmt.setBigDecimal(39, request.factQtyPost)
            } else {
                stmt.setNull(39, Types.NUMERIC)
            }

            // 40. DATE_PRODUCTION_ in
            if (request.dateProduction != null) {
                stmt.setDate(40, java.sql.Date.valueOf(request.dateProduction))
            } else {
                stmt.setNull(40, Types.DATE)
            }

            // 41. QUANTDOC_ in
            if (request.quantDoc != null) {
                stmt.setBigDecimal(41, request.quantDoc)
            } else {
                stmt.setNull(41, Types.NUMERIC)
            }

            // 42. SUMMDOC_ in
            if (request.summdoc != null) {
                stmt.setBigDecimal(42, request.summdoc)
            } else {
                stmt.setNull(42, Types.NUMERIC)
            }

            stmt.execute()

            OrderSpecUpdateResponse(
                stmt.getLong(1)
            )
        }

    }

}