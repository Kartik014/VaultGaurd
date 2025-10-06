package com.example.VaultGuard.DTO

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class EditTableDTO(
    @field:NotBlank(message = "Type is required")
    val type: String,

    @field:NotBlank(message = "Database ID is required")
    val dbId: String,

    @field:NotBlank(message = "Table name is required")
    val tableName: String,

    @field:NotEmpty(message = "Row identifier cannot be empty")
    val rowIdentifier: Map<String, Any>,

    @field:NotEmpty(message = "Column updates cannot be empty")
    val columnUpdates: Map<String, Any>
)
