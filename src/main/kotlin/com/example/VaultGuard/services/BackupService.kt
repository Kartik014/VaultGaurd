package com.example.VaultGuard.services

import com.example.VaultGuard.DTO.DatabaseBackupDTO
import com.example.VaultGuard.Interfaces.BackupInterface
import com.example.VaultGuard.factory.BackupPolicyHandler
import com.example.VaultGuard.factory.DumpServiceDispatcher
import com.example.VaultGuard.models.ApiResponse
import com.example.VaultGuard.models.DatabaseBackup
import com.example.VaultGuard.models.DatabaseConnection
import com.example.VaultGuard.models.User
import com.example.VaultGuard.repository.BackupRepo
import com.example.VaultGuard.repository.DatabaseConnectionRepo
import com.example.VaultGuard.utils.ExtensionFunctions.string
import com.example.VaultGuard.utils.JwtUtils
import com.example.VaultGuard.utils.enums.DbNames
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service

@Service
class BackupService(private val storageService: StorageService, private val backupRepo: BackupRepo, private val backupPolicyHandler: BackupPolicyHandler, private val jwtUtils: JwtUtils, private val entityManager: EntityManager, private val dumpServiceDispatcher: DumpServiceDispatcher, private val databaseConnectionRepo: DatabaseConnectionRepo): BackupInterface {
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

    override fun getBackupPolicies(dbid: String): ApiResponse<List<DatabaseBackup>> {
        val backupPolicyList : List<DatabaseBackup> = backupRepo.findByDatabaseConnectionId(dbid)
        return ApiResponse(
            status = "success",
            message = "Backup policies fetched successfully",
            data = backupPolicyList
        )
    }

    override fun createBackup(dbid: String, databaseBackupDTO: DatabaseBackupDTO): ApiResponse<String> {
        val policy = backupRepo.findById(databaseBackupDTO.backupid!!).orElseThrow {
            IllegalArgumentException("Policy not found")
        }
        val userid = policy.user.id
        val connection = databaseConnectionRepo.connectAndFetchDataForBackup(dbid)
        val tables = policy.selectedtables?.split(",")?.map { it.trim() } ?: listOf("all")
        val dumpService = dumpServiceDispatcher.getServiceFor(connection.dbtype!!.lowercase())
        val backupFile = dumpService.dumpDatabase(connection, tables, 0)
        val byteArrayFile: ByteArray = backupFile.readBytes()
        val contentType: String = when (connection.dbtype.lowercase()) {
            DbNames.POSTGRES.string(), DbNames.MYSQL.string() -> "text/plain"
            DbNames.MONGODB.string() -> "application/zip"
            else -> "application/json"
        }
        val storageUrl = when (policy.storagetype.lowercase()) {
            "local" -> storageService.uploadFile(userid, dbid, backupFile.name, byteArrayFile, contentType).block()
            "supabase" -> storageService.uploadFile(userid, dbid, backupFile.name, byteArrayFile, contentType).block()
            else -> throw IllegalArgumentException("Unknown storage type: ${policy.storagetype}")
        }
        return ApiResponse(
            status = "success",
            message = "Backup created successfully",
            data = storageUrl
        )
    }
}