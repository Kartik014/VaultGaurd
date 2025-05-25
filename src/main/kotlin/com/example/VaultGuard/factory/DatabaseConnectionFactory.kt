package com.example.VaultGuard.factory

import com.example.VaultGuard.DTO.DbConnDTO
import com.example.VaultGuard.models.DatabaseConnection
import com.example.VaultGuard.models.User
import com.example.VaultGuard.utils.AESUtils
import org.springframework.stereotype.Component

@Component
class DatabaseConnectionFactory(private val aesUtils: AESUtils) {
    fun createDbConn(dbConnDTO: DbConnDTO, userRef: User, index: Long): DatabaseConnection{
        return DatabaseConnection(
            dbid = dbConnDTO.userid + "_" + index,
            user = userRef,
            dbtype = dbConnDTO.dbtype!!.lowercase(),
            host = dbConnDTO.host,
            port = dbConnDTO.port,
            dbname = dbConnDTO.dbname,
            username = dbConnDTO.username,
            password = aesUtils.encrypt(dbConnDTO.password!!),
            ssl = dbConnDTO.ssl!!
        )
    }
}