package com.example.VaultGuard.repository

import com.example.VaultGuard.factory.DatabaseHandlerFactory
import com.example.VaultGuard.models.DatabaseConnection
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class CustomDatabaseConnectionRepoImpl(private val entityManager: EntityManager, private val databaseHandlerFactory: DatabaseHandlerFactory): CustomDatabaseConnectionRepo {
    override fun getDbData(dbid: String): Map<String, Map<String, Any>> {
        val db = entityManager.find(DatabaseConnection::class.java, dbid)
            ?: throw IllegalArgumentException("Database connection not found")

        return databaseHandlerFactory.getHandler(db).connectAndFetchData(db)
    }
}