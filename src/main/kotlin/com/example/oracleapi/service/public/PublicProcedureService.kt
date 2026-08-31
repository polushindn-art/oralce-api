package com.example.oracleapi.service.public

import com.example.oracleapi.dto.public.GenIdResponse
import com.example.oracleapi.dto.public.GetNomenByBarcodeRequest
import com.example.oracleapi.dto.public.GetNomenByBarcodeResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PublicProcedureService(
    private val getNomenByBarcodeProcedure: PublicGetNomenByBarcodeProcedure,
    private val genIdRnProcedur: PublicGenIdRnProcedur
) {

    @Transactional(readOnly = true)
    fun getNomenByBarcode(request: GetNomenByBarcodeRequest): GetNomenByBarcodeResponse =
        getNomenByBarcodeProcedure.getNomenByBarcodeProcedure(request.barcode)

    @Transactional(readOnly = true)
    fun getIdRn(): GenIdResponse = genIdRnProcedur.take()

}
