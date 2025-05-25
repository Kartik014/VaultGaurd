package com.example.VaultGuard.repository

import com.example.VaultGuard.repository.CustomDatabaseConnectionRepo
import com.example.VaultGuard.models.DatabaseConnection
import com.example.VaultGuard.models.User
import org.springframework.data.jpa.repository.JpaRepository

interface DatabaseConnectionRepo: JpaRepository<DatabaseConnection, String>, CustomDatabaseConnectionRepo  {

    fun countByUser(user: User): Long
}