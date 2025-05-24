package com.example.VaultGuard.Interfaces

import com.example.VaultGuard.DTO.UserDTO
import com.example.VaultGuard.models.ApiResponse
import com.example.VaultGuard.models.User

interface AuthServiceInterface {

    fun signUp(userDTO: UserDTO): ApiResponse<User>

    fun logIn(userDTO: UserDTO): ApiResponse<String>

    fun findByEmail(email: String): User?
}