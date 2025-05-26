package com.example.VaultGuard.Interfaces

import com.example.VaultGuard.DTO.DatabaseBackupDTO
import com.example.VaultGuard.models.ApiResponse
import com.example.VaultGuard.models.DatabaseBackup

interface BackupInterface {

    fun createBackupPolicy(databaseBackupDTO: DatabaseBackupDTO): ApiResponse<DatabaseBackup>
}