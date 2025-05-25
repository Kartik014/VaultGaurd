package com.example.VaultGuard.services

import com.example.VaultGuard.DTO.DbConnDTO
import com.example.VaultGuard.Interfaces.DatabaseConnectionInterface
import com.example.VaultGuard.factory.DatabaseConnectionFactory
import com.example.VaultGuard.models.ApiResponse
import com.example.VaultGuard.models.DatabaseConnection
import com.example.VaultGuard.models.User
import com.example.VaultGuard.repository.DatabaseConnectionRepo
import com.example.VaultGuard.utils.JwtUtils
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service

@Service
class DatabaseConnectionService(private val databaseConnectionRepo: DatabaseConnectionRepo, private val entityManager: EntityManager, private val databaseConnectionFactory: DatabaseConnectionFactory, private val jwtUtils: JwtUtils): DatabaseConnectionInterface {
    override fun addDbConnection(dbConnDTO: DbConnDTO): ApiResponse<DatabaseConnection> {
        val userid = jwtUtils.getCurrentUserId()
        val userRef = entityManager.getReference(User::class.java, userid)
        val count = databaseConnectionRepo.countByUser(userRef)
        val index = count + 1
        dbConnDTO.userid = userid
        val newDbConn: DatabaseConnection = databaseConnectionFactory.createDbConn(dbConnDTO, userRef, index)
        val savedDbConn: DatabaseConnection = databaseConnectionRepo.save(newDbConn)
        return ApiResponse(
            status = "success",
            message = "Database information stored successfully",
            data = savedDbConn
        )
    }

    override fun removeDbConnection(): ApiResponse<String> {
        TODO("Not yet implemented")
    }

    override fun updateDbConnection(): ApiResponse<DatabaseConnection> {
        TODO("Not yet implemented")
    }

    override fun getAllDb(): ApiResponse<List<DatabaseConnection>> {
        val DbConnectionList: List<DatabaseConnection> = databaseConnectionRepo.findAll()
        return ApiResponse(
            status = "success",
            message = "All Databases fetched successfully",
            data = DbConnectionList
        )
    }

    override fun connectDb(dbid: String): ApiResponse<Map<String, Map<String, Any>>> {
        val DbConnectionResult = databaseConnectionRepo.getDbData(dbid)
        return ApiResponse(
            status = "success",
            message = "Database connected successfully",
            data = DbConnectionResult
        )
    }
}