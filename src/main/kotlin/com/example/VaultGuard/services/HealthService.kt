package com.example.VaultGuard.services

import com.example.VaultGuard.Interfaces.HealthServiceInterface
import com.example.VaultGuard.models.ApiResponse
import com.example.VaultGuard.repository.DatabaseConnectionRepo
import org.springframework.stereotype.Service
import java.lang.management.ManagementFactory
import java.time.Instant

@Service
class HealthService(private val databaseConnectionRepo: DatabaseConnectionRepo) : HealthServiceInterface {

    private val startTime: Instant = Instant.now()

    override fun checkHealth(): ApiResponse<Any> {
        val runtime = Runtime.getRuntime()
        val memoryUsage = mapOf(
            "totalMemory" to "${runtime.totalMemory() / 1024 / 1024} MB",
            "freeMemory" to "${runtime.freeMemory() / 1024 / 1024} MB",
            "maxMemory" to "${runtime.maxMemory() / 1024 / 1024} MB"
        )

        val uptimeMillis = ManagementFactory.getRuntimeMXBean().uptime

        val dbStatus = try {
            databaseConnectionRepo.count()
            "UP"
        } catch (e: Exception) {
            "DOWN: ${e.message}"
        }

        val healthData = mapOf(
            "status" to "UP",
            "serverStartTime" to startTime.toString(),
            "uptimeMillis" to uptimeMillis,
            "memoryUsageMB" to memoryUsage,
            "databaseStatus" to dbStatus
        )

        return ApiResponse(
            status = "success",
            message = "Health check successful",
            data = healthData
        )
    }
}