package com.example.VaultGuard.controllers

import com.example.VaultGuard.DTO.DatabaseBackupPolicyDTO
import com.example.VaultGuard.models.ApiResponse
import com.example.VaultGuard.models.DatabaseBackupPolicy
import com.example.VaultGuard.Interfaces.BackupInterface
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
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
    fun createBackupPolicy(@RequestBody databaseBackupPolicyDTO: DatabaseBackupPolicyDTO): ResponseEntity<ApiResponse<DatabaseBackupPolicy>> {
        return try {
            val createdBackupPolicy = backupService.createBackupPolicy(databaseBackupPolicyDTO)
            ResponseEntity(createdBackupPolicy,HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }

    @GetMapping("/v1/{dbid}/get-policy")
    fun getBackupPolicies(@PathVariable dbid: String): ResponseEntity<ApiResponse<List<DatabaseBackupPolicy>>> {
        return try {
            val backupPolicyList = backupService.getBackupPolicies(dbid)
            ResponseEntity(backupPolicyList,HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }

    @GetMapping("/v1/{dbid}/create-backup")
    fun createBackup(@PathVariable dbid: String, @RequestBody databaseBackupPolicyDTO: DatabaseBackupPolicyDTO): ResponseEntity<ApiResponse<String>>{
        return try {
            val backupLink = backupService.createBackup(dbid, databaseBackupPolicyDTO)
            ResponseEntity(backupLink, HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }

    @GetMapping("/v1/{dbid}/list-backup-files")
    fun listBackupFiles(@PathVariable dbid: String): ResponseEntity<ApiResponse<List<Map<String, Any>>>> {
        return try {
            val backupFiles = backupService.listBackupFiles(dbid)
            ResponseEntity(backupFiles, HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }
}