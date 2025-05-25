package com.example.VaultGuard.DTO

data class DbConnDTO(
    val dbid: String? = null,
    var userid: String? = null,
    val dbtype: String? = null,
    val host: String? = null,
    val port: Int? = null,
    val dbname: String? = null,
    val username: String? = null,
    val password: String? = null,
    val ssl: Boolean? = false
)
