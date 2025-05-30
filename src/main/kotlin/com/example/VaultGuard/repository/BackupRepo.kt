package com.example.VaultGuard.repository

import com.example.VaultGuard.models.DatabaseBackup
import com.example.VaultGuard.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface BackupRepo: JpaRepository<DatabaseBackup, String> {

    fun countByUser(user: User): Long

    @Query("SELECT b FROM DatabaseBackup b WHERE b.databaseConnection.dbid = :dbid")
    fun findByDatabaseConnectionId(@Param("dbid") dbid: String): List<DatabaseBackup>
}