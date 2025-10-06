package com.example.VaultGuard.DTO

import jakarta.validation.constraints.Min

data class PageLimitDTO(
    @field:Min(value = 0, message = "Page must be zero or positive")
    val page: Int = 0,

    @field:Min(value = 1, message = "Limit must be at least 1")
    val limit: Int = 0
)
