package com.example.VaultGuard.handler.sql

import com.example.VaultGuard.DTO.AddRowDataDTO
import com.example.VaultGuard.DTO.EditTableDTO
import com.example.VaultGuard.DTO.FetchTableDTO
import com.example.VaultGuard.DTO.RemoveRowDataDTO
import com.example.VaultGuard.Interfaces.GenericHandlerInterface
import com.example.VaultGuard.factory.DatabaseConnectionFactory
import com.example.VaultGuard.models.DatabaseConnection
import com.example.VaultGuard.utils.ExtensionFunctions.string
import com.example.VaultGuard.utils.enums.DbNames
import org.springframework.stereotype.Component

@Component
class SqlDatabaseHandler(private val databaseConnectionFactory: DatabaseConnectionFactory) : GenericHandlerInterface {
    override fun connectAndFetchData(db: DatabaseConnection, fetchTableDTO: FetchTableDTO): Map<String, Map<String, Any>> {
        val dbConn = databaseConnectionFactory.connectDb(db)

        val schemaPattern = when (db.dbtype!!.lowercase()) {
            DbNames.POSTGRES.string() -> "public"
            DbNames.MYSQL.string() -> db.dbname
            else -> null
        }

        val tableName = fetchTableDTO.tableName.toString()
        val page = fetchTableDTO.page
        val limit = fetchTableDTO.limit

        val result = mutableMapOf<String, Map<String, Any>>()

        dbConn.use { connection ->
            val metadata = connection!!.metaData
            val tableSchema = schemaPattern

            val pk = mutableSetOf<String>()
            val fk = mutableSetOf<String>()
            val unique = mutableSetOf<String>()

            metadata.getPrimaryKeys(null, tableSchema, tableName).use { rs ->
                while (rs.next()) pk.add(rs.getString("COLUMN_NAME"))
            }

            metadata.getImportedKeys(null, tableSchema, tableName).use { rs ->
                while (rs.next()) fk.add(rs.getString("FKCOLUMN_NAME"))
            }

            metadata.getIndexInfo(null, tableSchema, tableName, true, false).use { rs ->
                while (rs.next()) rs.getString("COLUMN_NAME")?.let { unique.add(it) }
            }

            val countStmt = connection.createStatement()
            val countRs = countStmt.executeQuery("SELECT COUNT(*) FROM \"$tableName\"")
            countRs.next()
            val totalRows = countRs.getInt(1)

            val stmt = connection.createStatement()
            val offset = page * limit
            val rs = stmt.executeQuery("SELECT * FROM \"$tableName\" LIMIT $limit OFFSET $offset")
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

            result[tableName] = mapOf("columns" to columns, "rows" to rows)
            result["pagination"] = mapOf(
                "totalRows" to totalRows,
                "page" to page,
                "limit" to limit,
                "hasPrevious" to (page > 0),
                "hasNext" to ((offset + limit) < totalRows)
            )
        }

        dbConn!!.close()

        return result
    }

    override fun fetchTableNames(db: DatabaseConnection): List<String> {
        val dbConn = databaseConnectionFactory.connectDb(db)

        val tableNames = mutableListOf<String>()

        val schemaPattern = when (db.dbtype!!.lowercase()) {
            DbNames.POSTGRES.string() -> "public"
            DbNames.MYSQL.string() -> db.dbname
            else -> null
        }

        dbConn.use { connection ->
            val metadata = connection!!.metaData
            val tables = metadata.getTables(null, schemaPattern, "%", arrayOf("TABLE"))

            while (tables.next()) {
                val tableName = tables.getString("TABLE_NAME")
                tableNames.add(tableName)
            }
        }

        dbConn!!.close()

        return tableNames
    }

    override fun editDbData(db: DatabaseConnection, editTableDTO: EditTableDTO): Int {
        val dbConn = databaseConnectionFactory.connectDb(db)

        val whereClause = editTableDTO.rowIdentifier.entries.joinToString(" AND ") { "${it.key} = ?" }
        val setClause = editTableDTO.columnUpdates.entries.joinToString(", ") { "${it.key} = ?" }

        var rowsUpdated: Int = 0
        val sql = "UPDATE ${editTableDTO.tableName} SET $setClause WHERE $whereClause"

        dbConn.use { connection ->
            connection!!.prepareStatement(sql).use { stmt ->
                var i = 1

                editTableDTO.columnUpdates.values.forEach { value ->
                    stmt.setObject(i++, value)
                }

                editTableDTO.rowIdentifier.values.forEach { value ->
                    stmt.setObject(i++, value)
                }

                rowsUpdated = stmt.executeUpdate()
            }
        }

        dbConn!!.close()

        return rowsUpdated
    }

    override fun addDataToDB(db: DatabaseConnection, addRowDataDTO: AddRowDataDTO): Boolean {
        val dbConn = databaseConnectionFactory.connectDb(db)

        val columns = addRowDataDTO.newData.keys.joinToString(", ")
        val placeholders = addRowDataDTO.newData.keys.joinToString(", ") { "?" }

        val sql = "INSERT INTO ${addRowDataDTO.tableName} ($columns) VALUES ($placeholders)"

        dbConn.use { connection ->
            connection!!.prepareStatement(sql).use { stmt ->
                var i = 1

                addRowDataDTO.newData.values.forEach { value ->
                    stmt.setObject(i++, value)
                }

                stmt.executeUpdate()
            }
        }

        dbConn!!.close()

        return true
    }

    override fun removeDataFromDB(db: DatabaseConnection, removeRowDataDTO: RemoveRowDataDTO): Boolean {
        val dbConn = databaseConnectionFactory.connectDb(db)

        val sql = "DELETE FROM ${removeRowDataDTO.tableName} WHERE ${removeRowDataDTO.removeDataKey.columnName} = ?"

        dbConn.use { connection ->
            connection!!.prepareStatement(sql).use { stmt ->
                stmt.setObject(1, removeRowDataDTO.removeDataKey.columnValue)
                stmt.executeUpdate()
            }
        }

        dbConn!!.close()

        return true
    }

    override fun fetchEditedData(db: DatabaseConnection, editTableDTO: EditTableDTO): Map<String, Any> {
        val dbConn = databaseConnectionFactory.connectDb(db)

        dbConn.use { connection ->
            val whereClause = editTableDTO.rowIdentifier.keys.joinToString(" AND ") { "$it = ?" }
            val sql = "SELECT * FROM ${editTableDTO.tableName} WHERE $whereClause"
            val stmt = connection!!.prepareStatement(sql)

            editTableDTO.rowIdentifier.values.forEachIndexed { i, value ->
                stmt.setObject(i + 1, value)
            }

            val rs = stmt.executeQuery()
            val meta = rs.metaData

            dbConn!!.close()

            return if (rs.next()) {
                (1..meta.columnCount).associate { index ->
                    meta.getColumnName(index) to rs.getObject(index)
                }
            } else {
                emptyMap()
            }
        }
    }

}
