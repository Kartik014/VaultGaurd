package com.example.VaultGuard.Interfaces

import com.example.VaultGuard.models.ApiResponse

interface HealthServiceInterface {

    fun checkHealth(): ApiResponse<Any>
}