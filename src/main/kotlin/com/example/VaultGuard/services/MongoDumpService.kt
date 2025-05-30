package com.example.VaultGuard.services

import com.example.VaultGuard.Interfaces.DatabaseDumpService
import com.example.VaultGuard.models.DatabaseConnection
import com.example.VaultGuard.utils.ExtensionFunctions.string
import com.example.VaultGuard.utils.enums.DbNames
import java.io.File

class MongoDumpService: DatabaseDumpService {
    override fun supports(dbtype: String): Boolean {
        return dbtype == DbNames.MONGODB.string()
    }

    override fun dumpDatabase(connection: DatabaseConnection, tables: List<String>, index: Long): File {
        val dir = File("backupfile-${connection.dbid}-$index")
        dir.mkdirs()

        val command = mutableListOf(
            "mongodump",
            "--host", connection.host,
            "--port", connection.port.toString(),
            "--username", connection.username,
            "--password", connection.password,
            "--db", connection.dbname,
            "--out", dir.absolutePath
        )

        if (!tables.contains("all")) {
            tables.forEach { command += listOf("--collection", it) }
        }

        val process = ProcessBuilder(command).start()
        process.waitFor()

        // Compress to zip
        val zipFile = File("${dir.name}.zip")
        ProcessBuilder("zip", "-r", zipFile.absolutePath, dir.name).start().waitFor()
        dir.deleteRecursively()

        return zipFile
    }
}