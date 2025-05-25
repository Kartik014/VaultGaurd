package com.example.VaultGuard.Interfaces

import com.example.VaultGuard.DTO.DbConnDTO
import com.example.VaultGuard.models.ApiResponse
import com.example.VaultGuard.models.DatabaseConnection

interface DatabaseConnectionInterface {

    fun addDbConnection(dbConnDTO: DbConnDTO): ApiResponse<DatabaseConnection>

    fun removeDbConnection(): ApiResponse<String>

    fun updateDbConnection(): ApiResponse<DatabaseConnection>

    fun getAllDb(): ApiResponse<List<DatabaseConnection>>

    fun connectDb(dbid: String): ApiResponse<Map<String, Map<String, Any>>>
}