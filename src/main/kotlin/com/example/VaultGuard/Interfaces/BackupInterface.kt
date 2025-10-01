package com.example.VaultGuard.Interfaces

import com.example.VaultGuard.DTO.DatabaseBackupPolicyDTO
import com.example.VaultGuard.models.ApiResponse
import com.example.VaultGuard.models.DatabaseBackupPolicy

interface BackupInterface {

    fun createBackupPolicy(databaseBackupPolicyDTO: DatabaseBackupPolicyDTO): ApiResponse<DatabaseBackupPolicy>

    fun getBackupPolicies(dbId: String): ApiResponse<List<DatabaseBackupPolicy>>

    fun createBackup(dbId: String, databaseBackupPolicyDTO: DatabaseBackupPolicyDTO): ApiResponse<String>

    fun listBackupFiles(dbId: String): ApiResponse<List<Map<String, Any>>>?
}