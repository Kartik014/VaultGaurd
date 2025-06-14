package com.example.VaultGuard.DTO

data class DbUpdateEvent(
    val userId: String,
    val dbId: String,
    val tablename: String,
    val data: Any
)
