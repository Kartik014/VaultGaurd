package com.example.VaultGuard.DTO

import jakarta.validation.constraints.NotBlank

data class RoleDTO(
    @field:NotBlank(message = "User ID is required")
    val userId: String? = "",

    @field:NotBlank(message = "New role is required")
    val newRole: String? = ""
)
