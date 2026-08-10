package com.example.oracleapi.service.picture

import com.example.oracleapi.entity.table.Picture
import com.example.oracleapi.repository.picture.PictureRepository
import org.springframework.stereotype.Service

@Service
class PictureService(
    private val pictureRepository: PictureRepository
) {
    fun getPictureByRn(rn: Long): Picture? {
        return pictureRepository.findById(rn).orElse(null)
    }

    fun getRnListByTablernNotDeleted(tablern: Long): List<Long> {
        return  pictureRepository.findRnByTablern(tablern)
            .filter { it.deleted == null }
            .map { it.rn }
    }
}