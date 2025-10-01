package com.example.VaultGuard.DTO

data class RemoveRowDataDTO(
    val type: String,
    val dbId: String,
    val tableName: String,
    val removeDataKey: removeDataParams
)

data class removeDataParams(
    val columnName: String,
    val columnValue: Any
)
