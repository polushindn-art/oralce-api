package com.example.oracleapi.service.picture

import com.example.oracleapi.entity.picture.Picture
import com.example.oracleapi.repository.picture.PictureRepository
import org.springframework.stereotype.Service

@Service
class PictureService(
    private val pictureRepository: PictureRepository
) {
    fun getPictureByRn(rn: Long): Picture? {
        return pictureRepository.findById(rn).orElse(null)
    }
}