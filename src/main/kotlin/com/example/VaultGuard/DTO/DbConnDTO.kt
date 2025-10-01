package com.example.VaultGuard.DTO

data class DbConnDTO(
    val dbId: String? = null,
    var userId: String? = null,
    val dbType: String? = null,
    val host: String? = null,
    val port: Int? = null,
    val dbName: String? = null,
    val userName: String? = null,
    val password: String? = null,
    val ssl: Boolean? = false
)
