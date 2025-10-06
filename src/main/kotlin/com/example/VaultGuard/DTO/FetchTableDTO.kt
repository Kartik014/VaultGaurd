package com.example.VaultGuard.DTO

import jakarta.validation.constraints.NotBlank

data class FetchTableDTO(
    @field:NotBlank(message = "Database ID is required")
    val dbId: String? = null,

    @field:NotBlank(message = "Table name is required")
    val tableName: String? = null,

    val type: String? = null,

    val page: Int = 0,

    val limit: Int = 10
)
