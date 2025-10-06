package com.example.VaultGuard.DTO

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank

data class RemoveRowDataDTO(
    @field:NotBlank(message = "Type is required")
    val type: String,

    @field:NotBlank(message = "Database ID is required")
    val dbId: String,

    @field:NotBlank(message = "Table name is required")
    val tableName: String,

    @field:Valid
    val removeDataKey: RemoveDataParams
)

data class RemoveDataParams(
    @field:NotBlank(message = "Column name is required")
    val columnName: String,

    @field:NotBlank(message = "Column value is required")
    val columnValue: Any
)
