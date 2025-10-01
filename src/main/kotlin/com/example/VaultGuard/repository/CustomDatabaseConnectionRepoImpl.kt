package com.example.VaultGuard.repository

import com.example.VaultGuard.DTO.AddRowDataDTO
import com.example.VaultGuard.DTO.EditTableDTO
import com.example.VaultGuard.DTO.FetchTableDTO
import com.example.VaultGuard.DTO.RemoveRowDataDTO
import com.example.VaultGuard.factory.DatabaseHandlerFactory
import com.example.VaultGuard.models.DatabaseConnection
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class CustomDatabaseConnectionRepoImpl(private val entityManager: EntityManager, private val databaseHandlerFactory: DatabaseHandlerFactory): CustomDatabaseConnectionRepo {
    override fun getDbData(dbId: String): List<String> {
        val db = entityManager.find(DatabaseConnection::class.java, dbId)
            ?: throw IllegalArgumentException("Database connection not found")

        return databaseHandlerFactory.getHandler(db).fetchTableNames(db)
    }

    override fun fetchTableData(fetchTableDTO: FetchTableDTO): Map<String, Map<String, Any>> {
        val db = entityManager.find(DatabaseConnection::class.java, fetchTableDTO.dbId)
            ?: throw IllegalArgumentException("Database connection not found")

        return databaseHandlerFactory.getHandler(db).connectAndFetchData(db, fetchTableDTO)
    }

    override fun connectAndFetchDataForBackup(dbId: String): DatabaseConnection {
        val db = entityManager.find(DatabaseConnection::class.java, dbId)
            ?: throw IllegalArgumentException("Database connection not found")

        return db
    }

    override fun editDbData(editTableDTO: EditTableDTO): Int {
        val db = entityManager.find(DatabaseConnection::class.java, editTableDTO.dbId)
            ?: throw IllegalArgumentException("Database connection not found")

        return databaseHandlerFactory.getHandler(db).editDbData(db, editTableDTO)
    }

    override fun addDataToDB(addRowDataDTO: AddRowDataDTO): Boolean {
        val db = entityManager.find(DatabaseConnection::class.java, addRowDataDTO.dbId)
            ?: throw IllegalArgumentException("Database connection not found")

        return databaseHandlerFactory.getHandler(db).addDataToDB(db, addRowDataDTO)
    }

    override fun removeDataFromDB(removeRowDataDTO: RemoveRowDataDTO): Boolean {
        val db = entityManager.find(DatabaseConnection::class.java, removeRowDataDTO.dbId)
            ?: throw java.lang.IllegalArgumentException("Database connection not found")

        return databaseHandlerFactory.getHandler(db).removeDataFromDB(db, removeRowDataDTO)
    }

    override fun fetchEditedData(editTableDTO: EditTableDTO): Map<String, Any> {
        val db = entityManager.find(DatabaseConnection::class.java, editTableDTO.dbId)
            ?: throw IllegalArgumentException("Database connection not found")

        return databaseHandlerFactory.getHandler(db).fetchEditedData(db, editTableDTO)
    }
}