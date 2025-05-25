package com.example.VaultGuard.repository

import com.example.VaultGuard.models.DatabaseConnection
import com.example.VaultGuard.utils.AESUtils
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import java.sql.DriverManager

@Repository
class CustomDatabaseConnectionRepoImpl(private val entityManager: EntityManager, private val aesUtils: AESUtils): CustomDatabaseConnectionRepo {
    override fun getDbData(dbid: String): Map<String, List<Map<String, Any>>> {
        val db = entityManager.find(DatabaseConnection::class.java, dbid)
            ?: throw IllegalArgumentException("Database connection not found")
        val dburl = when (db.dbtype!!.lowercase()) {
            "postgres" -> "jdbc:postgresql://${db.host}:${db.port}/${db.dbname}"
            "mysql" -> "jdbc:mysql://${db.host}:${db.port}/${db.dbname}"
            else -> throw IllegalArgumentException("Unsupported DB type: ${db.dbtype}")
        }
        val schemaPattern = when (db.dbtype.lowercase()) {
            "postgres" -> "public"
            "mysql" -> null
            else -> null
        }
        val result = mutableMapOf<String, List<Map<String, Any>>>()
        val decryptedPassword = aesUtils.decrypt(db.password)
        DriverManager.getConnection(dburl, db.username, decryptedPassword).use { connection ->
            val metadata = connection.metaData
            val tables = metadata.getTables(null, schemaPattern, "%", arrayOf("TABLE"))

            while (tables.next()) {
                val tableName = tables.getString("TABLE_NAME")
                val stmt = connection.createStatement()
                val rs = stmt.executeQuery("SELECT * FROM $tableName LIMIT 100")
                val meta = rs.metaData
                val columnCount = meta.columnCount

                val rows = mutableListOf<Map<String, Any>>()
                while (rs.next()) {
                    val row = mutableMapOf<String, Any>()
                    for (i in 1..columnCount) {
                        row[meta.getColumnName(i)] = rs.getObject(i) ?: "null"
                    }
                    rows.add(row)
                }

                result[tableName] = rows
            }
        }

        return result
    }
}