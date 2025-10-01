package com.example.VaultGuard.repository

import com.example.VaultGuard.DTO.AddRowDataDTO
import com.example.VaultGuard.DTO.EditTableDTO
import com.example.VaultGuard.DTO.FetchTableDTO
import com.example.VaultGuard.DTO.RemoveRowDataDTO
import com.example.VaultGuard.models.DatabaseConnection

interface CustomDatabaseConnectionRepo {
    fun getDbData(dbId: String): List<String>

    fun fetchTableData(fetchTableDTO: FetchTableDTO): Map<String, Map<String, Any>>

    fun connectAndFetchDataForBackup(dbId: String): DatabaseConnection

    fun editDbData(editTableDTO: EditTableDTO): Int

    fun addDataToDB(addRowDataDTO: AddRowDataDTO): Boolean

    fun removeDataFromDB(removeRowDataDTO: RemoveRowDataDTO): Boolean

    fun fetchEditedData(editTableDTO: EditTableDTO): Map<String, Any>
}