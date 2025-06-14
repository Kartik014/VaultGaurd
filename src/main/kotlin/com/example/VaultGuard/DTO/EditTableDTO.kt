package com.example.VaultGuard.DTO

data class EditTableDTO(
    val type: String,
    val dbid: String,
    val tablename: String,
    val rowidentifier: Map<String, Any>,
    val columnupdates: Map<String, Any>
)
