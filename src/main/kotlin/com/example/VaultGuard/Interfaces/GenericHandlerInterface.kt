package com.example.VaultGuard.Interfaces

import com.example.VaultGuard.models.DatabaseConnection

interface GenericHandlerInterface {
    fun connectAndFetchData(db: DatabaseConnection, tablename: String): Map<String, Map<String, Any>>

    fun fetchTableNames(db: DatabaseConnection): List<String>
}