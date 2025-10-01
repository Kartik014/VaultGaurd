package com.example.VaultGuard.DTO

data class UserDTO(
    val id: String? = null,
    val userName: String = "",
    val email: String = "",
    val password: String = "",
    var role: String = "",
    var createdBy: String = "",
    var mustChangePassword: Boolean = false
)
