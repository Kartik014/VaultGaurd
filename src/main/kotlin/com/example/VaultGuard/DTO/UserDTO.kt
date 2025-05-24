package com.example.VaultGuard.DTO

data class UserDTO(
    val id: String? = null,
    val username: String = "",
    val email: String = "",
    val password: String = "",
    var role: String = "",
    var createdby: String = "",
    var mustchangepassword: Boolean = false
)
