package com.example.VaultGuard.Interfaces

import com.example.VaultGuard.DTO.EditTableDTO
import com.example.VaultGuard.models.DatabaseConnection

interface GenericHandlerInterface {
    fun connectAndFetchData(db: DatabaseConnection, tablename: String): Map<String, Map<String, Any>>

    fun fetchTableNames(db: DatabaseConnection): List<String>

    fun editDbData(db: DatabaseConnection, editTableDTO: EditTableDTO): Int

    fun fetchEditedData(db: DatabaseConnection, editTableDTO: EditTableDTO): Map<String, Any>
}