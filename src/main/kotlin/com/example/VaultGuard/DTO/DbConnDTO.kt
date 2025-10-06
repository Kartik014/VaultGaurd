package com.example.VaultGuard.DTO

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class DbConnDTO(
    val dbId: String? = null,

    var userId: String? = null,

    @field:NotBlank(message = "Database type is required")
    val dbType: String? = null,

    @field:NotBlank(message = "Host is required")
    val host: String? = null,

    @field:NotNull(message = "Port is required")
    val port: Int? = null,

    @field:NotBlank(message = "Database name is required")
    val dbName: String? = null,

    @field:NotBlank(message = "Username is required")
    val userName: String? = null,

    @field:NotBlank(message = "Password is required")
    val password: String? = null,

    val ssl: Boolean? = false
)
