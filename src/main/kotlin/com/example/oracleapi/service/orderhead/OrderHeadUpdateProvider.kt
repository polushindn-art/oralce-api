package com.example.oracleapi.service.orderhead

import com.example.oracleapi.dto.orderhead.provider.OrderHeadProviderUpdateResponse
import com.example.oracleapi.dto.orderhead.provider.OrderHeadUpdateProviderRequest
import com.example.oracleapi.repository.orderhead.OrderheadRepository
import com.example.oracleapi.service.agnList.AgnListService
import org.springframework.stereotype.Component

@Component
class OrderHeadUpdateProvider(
    private val orderheadRepository: OrderheadRepository,
    private val agnListService: AgnListService
) {
    fun update(request: OrderHeadUpdateProviderRequest): OrderHeadProviderUpdateResponse {

        /*Получаем заказ*/
        val orderheadCurrent = orderheadRepository.findByRn(request.orderhead)
            ?: throw IllegalArgumentException("Заказ с RN:${request.orderhead} не найден")

        /*Получаем поставщика который сейчас*/
        val providerOld = agnListService.getByRn(orderheadCurrent.provider ?: 0)

        if (providerOld.rn == request.provider) {
            throw IllegalArgumentException("Поставщика с RN: ${request.provider} уже назначен на заказ")
        }

        if (!agnListService.existsById(request.provider)) {
            throw IllegalArgumentException("Поставщика с RN: ${request.provider} не существует")
        }

        val update = orderheadRepository.updateProvider(
            request.orderhead,
            request.provider
        )
        if (update == 0) {
            throw IllegalArgumentException("Заказ с RN:${request.orderhead} не найден")
        }

        val providerNew = agnListService.getByRn(request.provider)

        return OrderHeadProviderUpdateResponse(
            request.orderhead,
            providerNew,
            providerOld

        )
    }


}