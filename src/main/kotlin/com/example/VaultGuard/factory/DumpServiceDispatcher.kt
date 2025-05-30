package com.example.VaultGuard.factory

import com.example.VaultGuard.Interfaces.DatabaseDumpService
import org.springframework.stereotype.Component

@Component
class DumpServiceDispatcher(private val services: List<DatabaseDumpService>) {
    fun getServiceFor(dbType: String): DatabaseDumpService {
        return services.firstOrNull { it.supports(dbType.lowercase()) }
            ?: throw IllegalArgumentException("No dump service for DB type: $dbType")
    }
}