package com.example.VaultGuard.repository

import com.example.VaultGuard.models.DatabaseBackup
import com.example.VaultGuard.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BackupRepo: JpaRepository<DatabaseBackup, String> {

    fun countByUser(user: User): Long
}