package com.example.VaultGuard.controllers

import com.example.VaultGuard.DTO.DbConnDTO
import com.example.VaultGuard.Interfaces.DatabaseConnectionInterface
import com.example.VaultGuard.models.ApiResponse
import com.example.VaultGuard.models.DatabaseConnection
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
@RequestMapping("/dbConn")
class DatabaseConnectionController(private val databaseConnectionService: DatabaseConnectionInterface) {

    @PostMapping("/addDb")
    @PreAuthorize("hasAuthority('superadmin')")
    fun addDbConn(@RequestBody dbConnDTO: DbConnDTO): ResponseEntity<ApiResponse<DatabaseConnection>> {
        return try {
            val newDbConnection = databaseConnectionService.addDbConnection(dbConnDTO)
            ResponseEntity(newDbConnection, HttpStatus.OK)
        } catch (e: IllegalArgumentException){
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }

    @GetMapping("/allDb")
    @PreAuthorize("hasAuthority('superadmin')")
    fun getAllDb(): ResponseEntity<ApiResponse<List<DatabaseConnection>>> {
        return try {
            val dbConnectionList = databaseConnectionService.getAllDb()
            ResponseEntity(dbConnectionList, HttpStatus.OK)
        } catch (e: IllegalArgumentException){
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }

    @GetMapping("/connect/{dbid}")
    fun connectDb(@PathVariable dbid: String): ResponseEntity<ApiResponse<List<String>>> {
        return try {
            val connectedDbResult = databaseConnectionService.connectDb(dbid)
            ResponseEntity(connectedDbResult, HttpStatus.OK)
        } catch (e: IllegalArgumentException){
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }
}