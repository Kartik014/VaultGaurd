package com.example.VaultGuard.repository

import com.example.VaultGuard.models.DatabaseConnection


interface CustomDatabaseConnectionRepo {
    fun getDbData(dbid: String): List<String>

    fun fetchTableData(dbid: String, tablename: String): Map<String, Map<String, Any>>

    fun connectAndFetchDataForBackup(dbid: String): DatabaseConnection
}