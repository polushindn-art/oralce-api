package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.service.ats.CallNotificationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/ats")
@Tag(name = "Уведомления от АТС", description = "Приём событий о звонках")
class CallNotificationController(
    private val notificationService: CallNotificationService
) : BaseController() {

    @GetMapping("/incoming-call")
    @Operation(summary = "Уведомление о входящем звонке")
    fun handleIncomingCall(
        @RequestParam("caller") callerNumber: String,
        @RequestParam("to") internalNumber: String
    ): MyApiResponse<String> {
        val success = notificationService.processIncomingCall(
            callerNumber = callerNumber,
            internalNumber = internalNumber
        )

        return if (success) {
            MyApiResponse.success(
                data = "Уведомление отправлено сотруднику на номер $internalNumber",
                message = "Успешно"
            )
        } else {
            MyApiResponse.unsuccess(
                message = "Не найден user_id для внутреннего номера: $internalNumber",
                data = null
            )
        }
    }
}