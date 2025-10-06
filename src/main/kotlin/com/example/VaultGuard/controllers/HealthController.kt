package com.example.VaultGuard.controllers

import com.example.VaultGuard.Interfaces.HealthServiceInterface
import com.example.VaultGuard.models.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/health")
class HealthController(private val healthService: HealthServiceInterface) {

    @GetMapping("/check")
    fun checkHealth(): ResponseEntity<ApiResponse<Any>> {
        return try {
            val healthStatus = healthService.checkHealth()
            ResponseEntity(healthStatus, HttpStatus.OK)
        } catch (e: IllegalArgumentException){
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }
}