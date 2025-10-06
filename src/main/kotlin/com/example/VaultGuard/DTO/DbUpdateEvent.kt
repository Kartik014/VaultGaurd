package com.example.VaultGuard.DTO

import jakarta.validation.constraints.NotBlank

data class DbUpdateEvent(
    @field:NotBlank(message = "User ID is required")
    val userId: String,

    @field:NotBlank(message = "Database ID is required")
    val dbId: String,

    @field:NotBlank(message = "Table name is required")
    val tableName: String,

    val data: Any
)
