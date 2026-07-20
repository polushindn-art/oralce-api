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
        getNomenByBarcodeProcedure.getNomenByBarcodeProcedure(request.barcode)

    fun getIdRn(): GenIdResponse = genIdRnProcedur.take()

}
