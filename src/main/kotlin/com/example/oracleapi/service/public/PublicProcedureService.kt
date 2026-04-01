package com.example.oracleapi.service.public

import com.example.oracleapi.dto.public.GenIdResponse
import com.example.oracleapi.dto.public.GetNomenByBarcodeRequest
import com.example.oracleapi.dto.public.GetNomenByBarcodeResponse
import org.springframework.stereotype.Service

@Service
class PublicProcedureService(
    private val getNomenByBarcodeProcedure: PublicGetNomenByBarcodeProcedure,
    private val genIdRnProcedur: PublicGenIdRnProcedur
) {
    fun getNomenByBarcode(request: GetNomenByBarcodeRequest): GetNomenByBarcodeResponse =
        getNomenByBarcodeProcedure.getNomen(request.barcode)

    fun getIdRn(): GenIdResponse = genIdRnProcedur.generateRn()

    fun generateMultipleRn(count: Int): GenIdResponse {
        require(count in 1..100) { "Count must be between 1 and 100" }
        return genIdRnProcedur.generateMultipleRn(count)
    }

}
