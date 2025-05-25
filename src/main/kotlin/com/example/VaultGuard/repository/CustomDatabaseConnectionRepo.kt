package com.example.VaultGuard.repository


interface CustomDatabaseConnectionRepo {
    fun getDbData(dbid: String): Map<String, Map<String, Any>>
}