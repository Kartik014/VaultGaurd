package com.example.VaultGuard.controllers

import com.example.VaultGuard.DTO.DatabaseBackupPolicyDTO
import com.example.VaultGuard.models.ApiResponse
import com.example.VaultGuard.models.DatabaseBackupPolicy
import com.example.VaultGuard.Interfaces.BackupInterface
import com.example.VaultGuard.validators.CreateBackupPolicyValidator
import com.example.VaultGuard.validators.CreateBackupValidator
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/backup")
class BackupController(private val backupService: BackupInterface) {

    @PostMapping("/v1/create-backup-policy")
    @PreAuthorize("hasAuthority('superadmin') or hasAuthority('admin')")
    fun createBackupPolicy(@Validated(CreateBackupPolicyValidator::class) @RequestBody databaseBackupPolicyDTO: DatabaseBackupPolicyDTO): ResponseEntity<ApiResponse<DatabaseBackupPolicy>> {
        return try {
            val createdBackupPolicy = backupService.createBackupPolicy(databaseBackupPolicyDTO)
            ResponseEntity(createdBackupPolicy,HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }

    @GetMapping("/v1/{dbId}/get-policy")
    fun getBackupPolicies(@PathVariable dbId: String): ResponseEntity<ApiResponse<List<DatabaseBackupPolicy>>> {
        return try {
            val backupPolicyList = backupService.getBackupPolicies(dbId)
            ResponseEntity(backupPolicyList,HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }

    @PostMapping("/v1/{dbId}/create-backup")
    fun createBackup(@PathVariable dbId: String, @Validated(CreateBackupValidator::class) @RequestBody databaseBackupPolicyDTO: DatabaseBackupPolicyDTO): ResponseEntity<ApiResponse<String>>{
        return try {
            val backupLink = backupService.createBackup(dbId, databaseBackupPolicyDTO)
            ResponseEntity(backupLink, HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }

    @GetMapping("/v1/{dbId}/list-backup-files")
    fun listBackupFiles(@PathVariable dbId: String): ResponseEntity<ApiResponse<List<Map<String, Any>>>> {
        return try {
            val backupFiles = backupService.listBackupFiles(dbId)
            ResponseEntity(backupFiles, HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }
}