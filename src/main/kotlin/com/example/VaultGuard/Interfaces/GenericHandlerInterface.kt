package com.example.VaultGuard.Interfaces

import com.example.VaultGuard.DTO.AddRowDataDTO
import com.example.VaultGuard.DTO.EditTableDTO
import com.example.VaultGuard.DTO.RemoveRowDataDTO
import com.example.VaultGuard.models.DatabaseConnection

interface GenericHandlerInterface {
    fun connectAndFetchData(db: DatabaseConnection, tableName: String): Map<String, Map<String, Any>>

    fun fetchTableNames(db: DatabaseConnection): List<String>

    fun editDbData(db: DatabaseConnection, editTableDTO: EditTableDTO): Int

    fun addDataToDB(db: DatabaseConnection, addRowDataDTO: AddRowDataDTO): Boolean

    fun removeDataFromDB(db: DatabaseConnection, removeRowDataDTO: RemoveRowDataDTO): Boolean

    fun fetchEditedData(db: DatabaseConnection, editTableDTO: EditTableDTO): Map<String, Any>
}