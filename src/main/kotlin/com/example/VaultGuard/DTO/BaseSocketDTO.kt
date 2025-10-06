package com.example.VaultGuard.DTO

import jakarta.validation.constraints.NotBlank

data class BaseSocketDTO(
    @field:NotBlank(message = "Type is required")
    val type: String
)
