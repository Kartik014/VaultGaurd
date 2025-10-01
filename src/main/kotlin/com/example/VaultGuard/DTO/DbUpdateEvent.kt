package com.example.VaultGuard.DTO

data class DbUpdateEvent(
    val userId: String,
    val dbId: String,
    val tableName: String,
    val data: Any
)
