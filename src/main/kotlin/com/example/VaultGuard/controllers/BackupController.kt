package com.example.VaultGuard.controllers

import com.example.VaultGuard.DTO.DatabaseBackupDTO
import com.example.VaultGuard.models.ApiResponse
import com.example.VaultGuard.models.DatabaseBackup
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
    fun createBackupPolicy(@RequestBody databaseBackupDTO: DatabaseBackupDTO): ResponseEntity<ApiResponse<DatabaseBackup>> {
        return try {
            val createdBackupPolicy = backupService.createBackupPolicy(databaseBackupDTO)
            ResponseEntity(createdBackupPolicy,HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }

    @GetMapping("/v1/{dbid}/get-policy")
    fun getBackupPolicies(@PathVariable dbid: String): ResponseEntity<ApiResponse<List<DatabaseBackup>>> {
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
    fun createBackup(@PathVariable dbid: String, @RequestBody databaseBackupDTO: DatabaseBackupDTO): ResponseEntity<ApiResponse<String>>{
        return try {
            val backupLink = backupService.createBackup(dbid, databaseBackupDTO)
            ResponseEntity(backupLink, HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }
}