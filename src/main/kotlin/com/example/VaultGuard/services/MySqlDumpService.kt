package com.example.VaultGuard.services

import com.example.VaultGuard.Interfaces.DatabaseDumpService
import com.example.VaultGuard.models.DatabaseConnection
import com.example.VaultGuard.utils.ExtensionFunctions.string
import com.example.VaultGuard.utils.enums.DbNames
import java.io.File

class MySqlDumpService: DatabaseDumpService {
    override fun supports(dbtype: String): Boolean {
        return dbtype == DbNames.MYSQL.string()
    }

    override fun dumpDatabase(connection: DatabaseConnection, tables: List<String>, index: Int): File {
        val file = File("backupfile-${connection.dbid}-$index.sql")
        val tableArgs = if (tables.contains("all")) "" else tables.joinToString(" ")
        val command = listOf(
            "mysqldump",
            "-h", connection.host,
            "-P", connection.port.toString(),
            "-u", connection.username,
            "-p${connection.password}",
            connection.dbname
        ) + tableArgs.split(" ")

        val process = ProcessBuilder(command)
            .start()
        val output = process.inputStream.readBytes()
        val exitCode = process.waitFor()

        if (exitCode != 0) {
            val error = process.errorStream.bufferedReader().readText()
            throw RuntimeException("pg_dump failed: $error")
        }
        val tempFile = File.createTempFile("backupfile-${connection.dbid}-$index", ".sql")
        tempFile.writeBytes(output)
        return file
    }
}