package com.example.VaultGuard.factory

import com.example.VaultGuard.DTO.UserDTO
import com.example.VaultGuard.models.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UserFactory(private val passwordEncoder: PasswordEncoder) {
    fun createUser(userDTO: UserDTO): User{
        return User(
            id = UUID.randomUUID().toString(),
            username = userDTO.userName,
            email = userDTO.email,
            password = passwordEncoder.encode(userDTO.password),
            role = userDTO.role.lowercase(),
            createdby = userDTO.createdBy,
            mustchangepassword = userDTO.mustChangePassword
        )
    }
}