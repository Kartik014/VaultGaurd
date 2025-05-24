package com.example.VaultGuard.handler

import com.example.VaultGuard.models.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity(
            ApiResponse(
                status = "error",
                message = e.message ?: "Invalid request"
            ),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity(
            ApiResponse(
                status = "error",
                message = "Something went wrong: ${e.message}"
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}