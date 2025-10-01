package com.example.VaultGuard.DTO

data class AddRowDataDTO (
    val type: String,
    val dbId: String,
    val tableName: String,
    val newData: Map<String, Any>
)