package com.example.VaultGuard.services

import com.example.VaultGuard.DTO.DbConnDTO
import com.example.VaultGuard.DTO.DbUpdateEvent
import com.example.VaultGuard.DTO.EditTableDTO
import com.example.VaultGuard.Interfaces.DatabaseConnectionInterface
import com.example.VaultGuard.factory.DatabaseConnectionFactory
import com.example.VaultGuard.models.ApiResponse
import com.example.VaultGuard.models.DatabaseConnection
import com.example.VaultGuard.models.User
import com.example.VaultGuard.repository.DatabaseConnectionRepo
import com.example.VaultGuard.utils.JwtUtils
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class DatabaseConnectionService(private val databaseConnectionRepo: DatabaseConnectionRepo, private val entityManager: EntityManager, private val databaseConnectionFactory: DatabaseConnectionFactory, private val jwtUtils: JwtUtils): DatabaseConnectionInterface {

    @Autowired
    lateinit var applicationEventPublisher: ApplicationEventPublisher

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

    override fun connectDb(dbid: String): ApiResponse<List<String>> {
        val DbConnectionResult = databaseConnectionRepo.getDbData(dbid)
        return ApiResponse(
            status = "success",
            message = "Database connected successfully",
            data = DbConnectionResult
        )
    }

    override fun fetchTableData(userId: String, dbid: String, tablename: String) {
        val dbTableData = databaseConnectionRepo.fetchTableData(dbid, tablename)

        applicationEventPublisher.publishEvent(DbUpdateEvent(userId, dbid, tablename, dbTableData))
    }

    override fun editDbData(editTableDTO: EditTableDTO): ApiResponse<Map<String, Any>> {
        val affectedRows = databaseConnectionRepo.editDbData(editTableDTO)
        return ApiResponse(
            status = "success",
            message = "Database data updated successfully",
            data = mapOf(
                "type" to "edit_data",
                "affectedRows" to affectedRows,
                "timestamp" to System.currentTimeMillis()
            )
        )
    }

    override fun fetchEditedData(userId: String, editTableDTO: EditTableDTO) {
        val editedData = databaseConnectionRepo.fetchEditedData(editTableDTO)
        applicationEventPublisher.publishEvent(DbUpdateEvent(userId, editTableDTO.dbid, editTableDTO.tablename, editedData))
    }
}