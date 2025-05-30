package com.example.VaultGuard.factory

import com.example.VaultGuard.DTO.DatabaseBackupDTO
import com.example.VaultGuard.models.DatabaseBackup
import com.example.VaultGuard.models.DatabaseConnection
import com.example.VaultGuard.models.User
import org.springframework.stereotype.Component

@Component
class BackupPolicyHandler {
    fun createPolicy(databaseBackupDTO: DatabaseBackupDTO, userRef: User, databaseRef: DatabaseConnection, selectedTables: String, count: Long): DatabaseBackup {
        return DatabaseBackup(
            backupid = "backup_" + count + "_" + databaseBackupDTO.dbid,
            user = userRef,
            databaseConnection = databaseRef,
            policyname = databaseBackupDTO.policyname,
            selectedtables = selectedTables,
            frequencycron = databaseBackupDTO.frequencycron!!,
            storagetype = databaseBackupDTO.storagetype!!,
            isactive = databaseBackupDTO.isactive!!
        )
    }
}