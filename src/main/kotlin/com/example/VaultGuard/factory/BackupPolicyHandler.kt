package com.example.VaultGuard.factory

import com.example.VaultGuard.DTO.DatabaseBackupPolicyDTO
import com.example.VaultGuard.models.DatabaseBackupPolicy
import com.example.VaultGuard.models.DatabaseConnection
import com.example.VaultGuard.models.User
import org.springframework.stereotype.Component

@Component
class BackupPolicyHandler {
    fun createPolicy(databaseBackupPolicyDTO: DatabaseBackupPolicyDTO, userRef: User, databaseRef: DatabaseConnection, selectedTables: String, count: Long): DatabaseBackupPolicy {
        return DatabaseBackupPolicy(
            policyid = "backup_" + count + "_" + databaseBackupPolicyDTO.dbid,
            user = userRef,
            databaseConnection = databaseRef,
            policyname = databaseBackupPolicyDTO.policyname,
            selectedtables = selectedTables,
            frequencycron = databaseBackupPolicyDTO.frequencycron!!,
            storagetype = databaseBackupPolicyDTO.storagetype!!,
            isactive = databaseBackupPolicyDTO.isactive!!
        )
    }
}