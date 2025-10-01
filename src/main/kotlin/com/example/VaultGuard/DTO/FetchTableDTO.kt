package com.example.VaultGuard.DTO

data class FetchTableDTO(
    val dbId: String? = null,
    val tableName: String? = null,
    val type: String? = null,
    val page: Int = 0,
    val limit: Int = 10
)
