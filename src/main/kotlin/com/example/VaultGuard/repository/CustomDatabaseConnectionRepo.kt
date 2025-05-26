package com.example.VaultGuard.repository


interface CustomDatabaseConnectionRepo {
    fun getDbData(dbid: String): List<String>

    fun fetchTableData(dbid: String, tablename: String): Map<String, Map<String, Any>>
}