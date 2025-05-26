package com.example.VaultGuard.handler.sql

import com.example.VaultGuard.Interfaces.GenericHandlerInterface
import com.example.VaultGuard.models.DatabaseConnection
import com.example.VaultGuard.utils.AESUtils
import com.example.VaultGuard.utils.enums.DbNames
import java.sql.DriverManager
import org.springframework.stereotype.Component

@Component
class SqlDatabaseHandler(private val aesUtils: AESUtils) : GenericHandlerInterface {
    override fun connectAndFetchData(db: DatabaseConnection, tablename: String): Map<String, Map<String, Any>> {
        val dbType = DbNames.valueOf(db.dbtype!!.uppercase())
        val dbUrl = when (dbType) {
            DbNames.POSTGRES -> "jdbc:postgresql://${db.host}:${db.port}/${db.dbname}"
            DbNames.MYSQL -> "jdbc:mysql://${db.host}:${db.port}/${db.dbname}"
            else -> throw IllegalArgumentException("Unsupported SQL DB: ${db.dbtype}")
        }

        val schemaPattern = if (dbType == DbNames.POSTGRES) "public" else null
        val decryptedPassword = aesUtils.decrypt(db.password)

        val result = mutableMapOf<String, Map<String, Any>>()

        DriverManager.getConnection(dbUrl, db.username, decryptedPassword).use { connection ->
            val metadata = connection.metaData
            val tableSchema = schemaPattern

            val pk = mutableSetOf<String>()
            val fk = mutableSetOf<String>()
            val unique = mutableSetOf<String>()

            metadata.getPrimaryKeys(null, tableSchema, tablename).use { rs ->
                while (rs.next()) pk.add(rs.getString("COLUMN_NAME"))
            }

            metadata.getImportedKeys(null, tableSchema, tablename).use { rs ->
                while (rs.next()) fk.add(rs.getString("FKCOLUMN_NAME"))
            }

            metadata.getIndexInfo(null, tableSchema, tablename, true, false).use { rs ->
                while (rs.next()) rs.getString("COLUMN_NAME")?.let { unique.add(it) }
            }

            val stmt = connection.createStatement()
            val rs = stmt.executeQuery("SELECT * FROM \"$tablename\" LIMIT 100")
            val meta = rs.metaData
            val colCount = meta.columnCount

            val rows = mutableListOf<Map<String, Any>>()
            val columns = mutableListOf<Map<String, Any>>()

            for (i in 1..colCount) {
                val colName = meta.getColumnName(i)
                val colType = meta.getColumnTypeName(i)
                columns.add(
                    mapOf(
                        "name" to colName,
                        "type" to colType,
                        "isPrimaryKey" to pk.contains(colName),
                        "isForeignKey" to fk.contains(colName),
                        "isUnique" to unique.contains(colName)
                    )
                )
            }

            while (rs.next()) {
                val row = mutableMapOf<String, Any>()
                for (i in 1..colCount) {
                    row[meta.getColumnName(i)] = rs.getObject(i) ?: "null"
                }
                rows.add(row)
            }

            result[tablename] = mapOf("columns" to columns, "rows" to rows)
        }

        return result
    }


    override fun fetchTableNames(db: DatabaseConnection): List<String> {
        val dbType = DbNames.valueOf(db.dbtype!!.uppercase())
        val dbUrl = when (dbType) {
            DbNames.POSTGRES -> "jdbc:postgresql://${db.host}:${db.port}/${db.dbname}"
            DbNames.MYSQL -> "jdbc:mysql://${db.host}:${db.port}/${db.dbname}"
            else -> throw IllegalArgumentException("Unsupported SQL DB: ${db.dbtype}")
        }

        val schemaPattern = if (dbType == DbNames.POSTGRES) "public" else null
        val decryptedPassword = aesUtils.decrypt(db.password)

        val tableNames = mutableListOf<String>()

        DriverManager.getConnection(dbUrl, db.username, decryptedPassword).use { connection ->
            val metadata = connection.metaData
            val tables = metadata.getTables(null, schemaPattern, "%", arrayOf("TABLE"))

            while (tables.next()) {
                val tableName = tables.getString("TABLE_NAME")
                tableNames.add(tableName)
            }
        }

        return tableNames
    }

}
