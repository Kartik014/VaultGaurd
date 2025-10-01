package com.example.VaultGuard.handler.sql

import com.example.VaultGuard.DTO.EditTableDTO
import com.example.VaultGuard.Interfaces.GenericHandlerInterface
import com.example.VaultGuard.factory.DatabaseConnectionFactory
import com.example.VaultGuard.models.DatabaseConnection
import com.example.VaultGuard.utils.ExtensionFunctions.string
import com.example.VaultGuard.utils.enums.DbNames
import org.springframework.stereotype.Component

@Component
class SqlDatabaseHandler(private val databaseConnectionFactory: DatabaseConnectionFactory) : GenericHandlerInterface {
    override fun connectAndFetchData(db: DatabaseConnection, tablename: String): Map<String, Map<String, Any>> {
        val dbConn = databaseConnectionFactory.connectDb(db)

        val schemaPattern = if (db.dbtype!!.lowercase() == DbNames.POSTGRES.string()) "public" else null

        val result = mutableMapOf<String, Map<String, Any>>()

        dbConn.use { connection ->
            val metadata = connection!!.metaData
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
        val dbConn = databaseConnectionFactory.connectDb(db)

        val tableNames = mutableListOf<String>()

        val schemaPattern = if (db.dbtype!!.lowercase() == DbNames.POSTGRES.string()) "public" else null
        dbConn.use { connection ->
            val metadata = connection!!.metaData
            val tables = metadata.getTables(null, schemaPattern, "%", arrayOf("TABLE"))

            while (tables.next()) {
                val tableName = tables.getString("TABLE_NAME")
                tableNames.add(tableName)
            }
        }

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
        return rowsUpdated
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
