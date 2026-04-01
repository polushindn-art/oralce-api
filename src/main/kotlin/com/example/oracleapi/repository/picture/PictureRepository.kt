package com.example.oracleapi.repository.picture

import com.example.oracleapi.entity.picture.Picture
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PictureRepository : JpaRepository<Picture, Long>