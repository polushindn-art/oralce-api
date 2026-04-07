package com.example.oracleapi.controller

import com.example.oracleapi.service.user.CustomUserDetailSrv
import com.example.oracleapi.dto.user.VUserList
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
@Tag(name = "pkg_userlist", description = "Процедуры пакета PKG USERLIST")
class UserController(private val userService: CustomUserDetailSrv) {
    @GetMapping
    fun getAllUsers(): List<VUserList> {
        return userService.getAllUser()
    }
    @GetMapping("/{usercode}")
    fun getUser(@PathVariable usercode: String): String {
        return userService.getOneUserByUserCode(usercode).toString()
    }

}