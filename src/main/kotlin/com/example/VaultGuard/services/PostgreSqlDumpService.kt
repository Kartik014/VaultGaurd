package com.example.VaultGuard.services

import com.example.VaultGuard.Interfaces.DatabaseDumpService
import com.example.VaultGuard.models.DatabaseConnection
import com.example.VaultGuard.utils.AESUtils
import com.example.VaultGuard.utils.ExtensionFunctions.string
import com.example.VaultGuard.utils.enums.DbNames
import org.springframework.stereotype.Service
import java.io.File

@Service
class PostgreSqlDumpService(private val aesUtils: AESUtils): DatabaseDumpService {
    override fun supports(dbtype: String): Boolean {
        return dbtype == DbNames.POSTGRES.string()
    }

    override fun dumpDatabase(connection: DatabaseConnection, tables: List<String>, index: Long): File {
        val file = File("backupfile-${connection.dbid}-$index.sql")
        val tableArgs = if (tables.contains("all")) emptyList() else tables.flatMap { listOf("-t", it) }
        val pass = aesUtils.decrypt(connection.password)
        val command = listOf(
            "pg_dump",
            "-h", connection.host,
            "-p", connection.port.toString(),
            "-U", connection.username,
            "-d", connection.dbname,
            "-f", file.absolutePath
        ) + tableArgs

        val env = mapOf("PGPASSWORD" to pass)
        val process = ProcessBuilder(command)
            .apply { environment().putAll(env) }
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