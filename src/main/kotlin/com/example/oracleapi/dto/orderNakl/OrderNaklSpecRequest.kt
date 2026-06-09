package com.example.oracleapi.dto.orderNakl

import jakarta.validation.constraints.Positive
import org.jetbrains.annotations.NotNull
import java.math.BigDecimal

data class OrderNaklSpecRequest(

    @field:NotNull
    @field:Positive(message = "должно быть больше 0")
    val prn: Long,                          // PRN_ - ссылка на заголовок документа

    @field:NotNull
    @field:Positive(message = "должно быть больше 0")
    val nomen: Long,                        // NOMEN_ - номенклатура

    @field:NotNull
    @field:Positive(message = "должно быть больше 0")
    val inprice: BigDecimal,                // INPRICE_ - цена входящая

    @field:NotNull
    @field:Positive(message = "должно быть больше 0")
    val quant: BigDecimal,                  // QUANT_ - количество

    @field:NotNull
    @field:Positive(message = "должно быть больше 0")
    val summ: BigDecimal,                   // SUMM_ - сумма

    val measalt: Long? = null,              // MEASALT_ - единица измерения альтернативная

    val quantalt: BigDecimal? = null,       // QUANTALT_ - количество альтернативное

    @field:NotNull
    @field:Positive(message = "должно быть больше 0")
    val ndsrate: BigDecimal,        // NDSRATE_ - ставка НДС

    @field:NotNull
    @field:Positive(message = "должно быть больше 0")
    val country: Long,              // COUNTRY_ - страна происхождения

    val gtd: String? = null,                // GTD_ - ГТД номер

    val isUpdate: Boolean = false,          // isUpdate - признак обновления (false = вставка, true = обновление)

    val isTrans: Boolean = false,           // isTrans - признак транзакции

    val changeOverHead: Int = 0             // ChangeOverHead - изменения в заголовке
)