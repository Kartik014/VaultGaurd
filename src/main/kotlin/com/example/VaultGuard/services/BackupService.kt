package com.example.VaultGuard.services

import com.example.VaultGuard.DTO.DatabaseBackupDTO
import com.example.VaultGuard.Interfaces.BackupInterface
import com.example.VaultGuard.factory.BackupPolicyHandler
import com.example.VaultGuard.models.ApiResponse
import com.example.VaultGuard.models.DatabaseBackup
import com.example.VaultGuard.models.DatabaseConnection
import com.example.VaultGuard.models.User
import com.example.VaultGuard.repository.BackupRepo
import com.example.VaultGuard.utils.JwtUtils
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service

@Service
class BackupService(private val storageService: StorageService, private val backupRepo: BackupRepo, private val backupPolicyHandler: BackupPolicyHandler, private val jwtUtils: JwtUtils, private val entityManager: EntityManager): BackupInterface {
    override fun createBackupPolicy(databaseBackupDTO: DatabaseBackupDTO): ApiResponse<DatabaseBackup> {
        val userid = jwtUtils.getCurrentUserId()
        val userRef = entityManager.getReference(User::class.java, userid)
        val databaseRef = entityManager.getReference(DatabaseConnection::class.java, databaseBackupDTO.dbid)
        val selectedTables: String? = databaseBackupDTO.selectedtables?.joinToString(",") ?: "all"
        var count = backupRepo.countByUser(userRef)
        count = count + 1
        val newBackupPolicy = backupPolicyHandler.createPolicy(databaseBackupDTO, userRef, databaseRef, selectedTables!!, count)
        val savedBackupPolicy: DatabaseBackup = backupRepo.save(newBackupPolicy)
        return ApiResponse(
            status = "success",
            message = "Backup policy created successfully",
            data = savedBackupPolicy
        )
    }
}