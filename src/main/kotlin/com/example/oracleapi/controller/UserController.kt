package com.example.oracleapi.controller

import com.example.oracleapi.service.user.CustomUserDetailSrv
import com.example.oracleapi.dto.user.UserList
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(private val userService: CustomUserDetailSrv) {
    @GetMapping
    fun getAllUsers(): List<UserList> {
        return userService.getAllUser()
    }
    @GetMapping("/{usercode}")
    fun getUser(@PathVariable usercode: String): String {
        return userService.getOneUserByUserCode(usercode).toString()
    }

}