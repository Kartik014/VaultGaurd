package com.example.VaultGuard.Interfaces

import com.example.VaultGuard.models.DatabaseConnection

interface GenericHandlerInterface {
    fun connectAndFetchData(db: DatabaseConnection): Map<String, Map<String, Any>>
}