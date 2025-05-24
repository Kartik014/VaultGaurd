package com.example.VaultGuard.models

data class ApiResponse<T>(
    val status: String,
    val message: String,
    val data: T? = null
)
