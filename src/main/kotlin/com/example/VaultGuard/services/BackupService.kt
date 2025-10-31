package com.example.VaultGuard.services

import com.example.VaultGuard.DTO.DatabaseBackupPolicyDTO
import com.example.VaultGuard.Interfaces.BackupInterface
import com.example.VaultGuard.factory.BackupPolicyHandler
import com.example.VaultGuard.factory.DumpServiceDispatcher
import com.example.VaultGuard.models.ApiResponse
import com.example.VaultGuard.models.DatabaseBackupPolicy
import com.example.VaultGuard.models.DatabaseConnection
import com.example.VaultGuard.models.User
import com.example.VaultGuard.repository.BackupRepo
import com.example.VaultGuard.repository.DatabaseConnectionRepo
import com.example.VaultGuard.utils.ExtensionFunctions.string
import com.example.VaultGuard.utils.JwtUtils
import com.example.VaultGuard.utils.enums.DbNames
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class BackupService(private val storageService: StorageService, private val backupRepo: BackupRepo, private val backupPolicyHandler: BackupPolicyHandler, private val jwtUtils: JwtUtils, private val entityManager: EntityManager, private val dumpServiceDispatcher: DumpServiceDispatcher, private val databaseConnectionRepo: DatabaseConnectionRepo, private val webClient: WebClient): BackupInterface {
    override fun createBackupPolicy(databaseBackupPolicyDTO: DatabaseBackupPolicyDTO): ApiResponse<DatabaseBackupPolicy> {
        val userid = jwtUtils.getCurrentUserId()
        val userRef = entityManager.getReference(User::class.java, userid)
        val databaseRef = entityManager.getReference(DatabaseConnection::class.java, databaseBackupPolicyDTO.dbId)
        val selectedTables: String = databaseBackupPolicyDTO.selectedTables?.joinToString(",") ?: "all"
        var count = backupRepo.countByUser(userRef)
        count += 1
        val newBackupPolicy = backupPolicyHandler.createPolicy(databaseBackupPolicyDTO, userRef, databaseRef, selectedTables, count)
        val savedBackupPolicy: DatabaseBackupPolicy = backupRepo.save(newBackupPolicy)

        return ApiResponse(
            status = "success",
            message = "Backup policy created successfully",
            data = savedBackupPolicy
        )
    }

    override fun getBackupPolicies(dbId: String): ApiResponse<List<DatabaseBackupPolicy>> {
        val backupPolicyList : List<DatabaseBackupPolicy> = backupRepo.findByDatabaseConnectionId(dbId)

        return ApiResponse(
            status = "success",
            message = "Backup policies fetched successfully",
            data = backupPolicyList
        )
    }

    override fun createBackup(dbId: String, databaseBackupPolicyDTO: DatabaseBackupPolicyDTO): ApiResponse<String> {
        val policy = backupRepo.findById(databaseBackupPolicyDTO.policyId!!).orElseThrow {
            IllegalArgumentException("Policy not found")
        }
        val userid = policy.user.id
        val connection = databaseConnectionRepo.connectAndFetchDataForBackup(dbId)
        val tables = policy.selectedtables?.split(",")?.map { it.trim() } ?: listOf("all")
        val dumpService = dumpServiceDispatcher.getServiceFor(connection.dbtype!!.lowercase())
        val count = listBackupFiles(dbId)?.data?.size ?: 0
        val index: Int = count + 1
        val backupFile = dumpService.dumpDatabase(connection, tables, index)
        val byteArrayFile: ByteArray = backupFile.readBytes()
        val contentType: String = when (connection.dbtype.lowercase()) {
            DbNames.POSTGRES.string(), DbNames.MYSQL.string() -> "text/plain"
            DbNames.MONGODB.string() -> "application/zip"
            else -> "application/json"
        }
        val storageUrl = when (policy.storagetype.lowercase()) {
            "local" -> storageService.uploadFile(userid, dbId, backupFile.name, byteArrayFile, contentType).block()
            "supabase" -> storageService.uploadFile(userid, dbId, backupFile.name, byteArrayFile, contentType).block()
            else -> throw IllegalArgumentException("Unknown storage type: ${policy.storagetype}")
        }

        backupFile.delete()

        return ApiResponse(
            status = "success",
            message = "Backup created successfully",
            data = storageUrl
        )
    }

    override fun listBackupFiles(dbId: String): ApiResponse<List<Map<String, Any>>>? {
        val bucketName = "backups"
        val userId = jwtUtils.getCurrentUserId()
        val prefix = "$userId/$dbId/"
        val requestBody = mapOf("prefix" to prefix)

        return webClient.post()
            .uri("/object/list/$bucketName")
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(List::class.java)
            .map { rawList ->
                (rawList as List<Map<String, Any>>).mapNotNull { fileMap ->
                    val metadata = fileMap["metadata"] as? Map<*, *>
                    val name = fileMap["name"] as? String
                    val id = fileMap["id"] as? String
                    val createdAt = fileMap["created_at"] as? String
                    val size = (metadata?.get("size") as? Number)?.toLong()
                    val mimetype = metadata?.get("mimetype") as? String
                    if (name != null && id != null && createdAt != null && size != null && mimetype != null) {
                        val readableSize = convertToReadableSize(size)
                        mapOf(
                            "name" to name,
                            "id" to id,
                            "created_at" to createdAt,
                            "size" to readableSize,
                            "mimetype" to mimetype
                        )
                    } else {
                        null
                    }
                }
            }
            .map { fileList ->
                ApiResponse(
                    status = "success",
                    message = "Backup files listed successfully",
                    data = fileList
                )
            }
            .block() as ApiResponse<List<Map<String, Any>>>?
    }

    private fun convertToReadableSize(bytes: Long): String {
        val kb = 1024.0
        val mb = kb * 1024
        val gb = mb * 1024

        return when {
            bytes >= gb -> String.format("%.2f GB", bytes / gb)
            bytes >= mb -> String.format("%.2f MB", bytes / mb)
            bytes >= kb -> String.format("%.2f KB", bytes / kb)
            else -> "$bytes B"
        }
    }

}