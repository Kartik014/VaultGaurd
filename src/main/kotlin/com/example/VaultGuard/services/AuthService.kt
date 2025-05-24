package com.example.VaultGuard.services

import com.example.VaultGuard.DTO.UserDTO
import com.example.VaultGuard.Interfaces.AuthServiceInterface
import com.example.VaultGuard.factory.UserFactory
import com.example.VaultGuard.models.ApiResponse
import com.example.VaultGuard.models.User
import com.example.VaultGuard.repository.AuthRepo
import org.springframework.stereotype.Service

@Service
class AuthService(private val authRepo: AuthRepo, private val userFactory: UserFactory): AuthServiceInterface {

    override fun signUp(userDTO: UserDTO): ApiResponse<User> {
        if(authRepo.findByEmail(userDTO.email) != null){
            throw IllegalArgumentException("Email already registered")
        }
        var newUser: User
        var savedUser: User
        if(authRepo.count() == 0L){
            userDTO.role = "SUPERADMIN"
            userDTO.createdby = "self"
            newUser = userFactory.createUser(userDTO)
            savedUser = authRepo.save(newUser)
        } else {
            newUser = userFactory.createUser(userDTO)
            savedUser = authRepo.save(newUser)
        }
        return ApiResponse(
            status = "success",
            message = "User ${savedUser.username} created successfully",
            data = savedUser
        )
    }
}