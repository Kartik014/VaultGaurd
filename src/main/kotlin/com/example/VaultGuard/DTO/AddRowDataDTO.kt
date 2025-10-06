package com.example.VaultGuard.DTO

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class AddRowDataDTO (
    @field:NotBlank(message = "Type is required")
    val type: String,

    @field:NotBlank(message = "Database ID is required")
    val dbId: String,

    @field:NotBlank(message = "Table name is required")
    val tableName: String,

    @field:NotEmpty(message = "New data cannot be empty")
    val newData: Map<String, Any>
)