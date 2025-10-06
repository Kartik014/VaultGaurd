package com.example.VaultGuard.DTO

import com.example.VaultGuard.validators.LogInValidator
import com.example.VaultGuard.validators.SignUpValidator
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserDTO(
    val id: String? = null,

    @field:NotBlank(message = "Username cannot be blank", groups = [SignUpValidator::class])
    val userName: String = "",

    @field:NotBlank(message = "Email cannot be blank", groups = [SignUpValidator::class, LogInValidator::class])
    @field:Email(message = "Email should be valid")
    val email: String = "",

    @field:NotBlank(message = "Password cannot be blank", groups = [SignUpValidator::class, LogInValidator::class])
    @field:Size(min = 8, message = "Password must be at least 8 characters long")
    val password: String = "",

    var role: String = "",

    var createdBy: String = "",

    var mustChangePassword: Boolean = false
)
