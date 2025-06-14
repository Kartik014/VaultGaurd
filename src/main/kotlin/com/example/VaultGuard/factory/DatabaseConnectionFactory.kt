package com.example.VaultGuard.factory

import com.example.VaultGuard.DTO.DbConnDTO
import com.example.VaultGuard.models.DatabaseConnection
import com.example.VaultGuard.models.User
import com.example.VaultGuard.utils.AESUtils
import com.example.VaultGuard.utils.enums.DbNames
import org.springframework.stereotype.Component
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.sql.Connection
import java.sql.DriverManager

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

    fun connectDb(db: DatabaseConnection): Connection? {
        val dbType = DbNames.valueOf(db.dbtype!!.uppercase())
        val dbUrl = when (dbType) {
            DbNames.POSTGRES -> "jdbc:postgresql://${db.host}:${db.port}/${db.dbname}"
            DbNames.MYSQL -> "jdbc:mysql://${db.host}:${db.port}/${db.dbname}"
            else -> throw IllegalArgumentException("Unsupported SQL DB: ${db.dbtype}")
        }

        val decryptedPassword = aesUtils.decrypt(db.password)

        return DriverManager.getConnection(dbUrl, db.username, decryptedPassword)
    }

    fun connectMongoDb(db: DatabaseConnection): String {
        val rawPassword = aesUtils.decrypt(db.password)

        val encodedUsername = URLEncoder.encode(db.username, StandardCharsets.UTF_8.toString())
        val encodedPassword = URLEncoder.encode(rawPassword, StandardCharsets.UTF_8.toString())

        val uri = "mongodb+srv://${encodedUsername}:${encodedPassword}@${db.host}/?retryWrites=true&w=majority&appName=Cluster0"

        return uri
    }
}