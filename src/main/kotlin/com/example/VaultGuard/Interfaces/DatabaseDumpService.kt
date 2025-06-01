package com.example.VaultGuard.Interfaces

import com.example.VaultGuard.models.DatabaseConnection
import java.io.File

interface DatabaseDumpService {

    fun supports(dbtype: String): Boolean

    fun dumpDatabase(connection: DatabaseConnection, tables: List<String>, index: Int): File
}