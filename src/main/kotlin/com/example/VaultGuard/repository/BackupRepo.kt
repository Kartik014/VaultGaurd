package com.example.VaultGuard.repository

import com.example.VaultGuard.models.DatabaseBackupPolicy
import com.example.VaultGuard.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface BackupRepo: JpaRepository<DatabaseBackupPolicy, String> {

    fun countByUser(user: User): Long

    @Query("SELECT b FROM DatabaseBackupPolicy b WHERE b.databaseConnection.dbid = :dbid")
    fun findByDatabaseConnectionId(@Param("dbid") dbid: String): List<DatabaseBackupPolicy>
}