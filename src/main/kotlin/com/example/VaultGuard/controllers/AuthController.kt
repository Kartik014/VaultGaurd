package com.example.VaultGuard.controllers

import com.example.VaultGuard.DTO.UserDTO
import com.example.VaultGuard.Interfaces.AuthServiceInterface
import com.example.VaultGuard.models.ApiResponse
import com.example.VaultGuard.models.User
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthServiceInterface) {

    @PostMapping("/signUp")
    fun signUp(@RequestBody userDTO: UserDTO): ResponseEntity<ApiResponse<User>>{
        return try {
            val newUser = authService.signUp(userDTO)
            ResponseEntity(newUser, HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }
}