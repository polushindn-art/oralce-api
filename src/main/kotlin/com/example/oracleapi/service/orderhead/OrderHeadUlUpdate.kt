package com.example.oracleapi.service.orderhead

import com.example.oracleapi.dto.orderhead.ul.OrderHeadUlUpdateRequest
import com.example.oracleapi.dto.orderhead.ul.OrderHeadUlUpdateResponse
import com.example.oracleapi.repository.orderhead.OrderheadRepository
import com.example.oracleapi.service.agnList.AgnListService
import org.springframework.stereotype.Component

@Component
class OrderHeadUlUpdate(
    private val orderheadRepository: OrderheadRepository,
    private val agnListService: AgnListService
) {

    fun update(request: OrderHeadUlUpdateRequest): OrderHeadUlUpdateResponse {

        if (!agnListService.isOurOrg(request.ul)) {
            throw IllegalArgumentException("Юридическое лицо с RN:${request.ul} не является нашим")
        }

        if (!agnListService.existsById(request.ul)) {
            throw IllegalArgumentException("Юридического лица с RN: ${request.ul} не существует")
        }

        val update = orderheadRepository.updateUl(request.orderhead, request.ul)

        if (update == 0) {
            throw IllegalArgumentException("Заказ с RN:${request.orderhead} yне найден")
        }

        val ul = agnListService.getByRn(request.ul)

        return OrderHeadUlUpdateResponse(
            request.orderhead,
            ul
        )

    }

}