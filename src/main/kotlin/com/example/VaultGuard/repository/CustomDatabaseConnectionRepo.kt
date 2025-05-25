package com.example.VaultGuard.repository


interface CustomDatabaseConnectionRepo {
    fun getDbData(dbid: String): Map<String, List<Map<String, Any>>>
}