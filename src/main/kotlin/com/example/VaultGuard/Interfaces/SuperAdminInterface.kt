package com.example.VaultGuard.Interfaces

import com.example.VaultGuard.DTO.RoleDTO
import com.example.VaultGuard.DTO.UserDTO
import com.example.VaultGuard.models.ApiResponse
import com.example.VaultGuard.models.User

interface SuperAdminInterface {

    fun addUser(userDTO: UserDTO): ApiResponse<String>

    fun getAllUsers(): ApiResponse<List<User>>

    fun updateRole(roleDTO: RoleDTO): ApiResponse<User>

    fun removeUser(userId: String): ApiResponse<List<User>>
}