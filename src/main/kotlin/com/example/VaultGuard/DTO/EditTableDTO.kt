package com.example.VaultGuard.DTO

data class EditTableDTO(
    val type: String,
    val dbId: String,
    val tableName: String,
    val rowIdentifier: Map<String, Any>,
    val columnUpdates: Map<String, Any>
)
