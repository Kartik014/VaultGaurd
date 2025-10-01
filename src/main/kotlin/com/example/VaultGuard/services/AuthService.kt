package com.example.VaultGuard.services

import com.example.VaultGuard.DTO.UserDTO
import com.example.VaultGuard.Interfaces.AuthServiceInterface
import com.example.VaultGuard.factory.AuthenticatorFactory
import com.example.VaultGuard.factory.UserFactory
import com.example.VaultGuard.models.ApiResponse
import com.example.VaultGuard.models.User
import com.example.VaultGuard.repository.AuthRepo
import com.example.VaultGuard.utils.ExtensionFunctions.string
import com.example.VaultGuard.utils.JwtUtils
import com.example.VaultGuard.utils.enums.UserRoles
import org.springframework.stereotype.Service

@Service
class AuthService(private val authRepo: AuthRepo, private val userFactory: UserFactory, private val jwtUtils: JwtUtils, private val authenticatorFactory: AuthenticatorFactory): AuthServiceInterface {

    override fun signUp(userDTO: UserDTO): ApiResponse<User> {
        if(authRepo.findByEmail(userDTO.email) != null){
            throw IllegalArgumentException("Email already registered")
        }

        var newUser: User
        var savedUser: User

        if(authRepo.count() == 0L){
            userDTO.role = UserRoles.SUPERADMIN.string()
            userDTO.createdBy = "self"
        }

        newUser = userFactory.createUser(userDTO)
        savedUser = authRepo.save(newUser)

        return ApiResponse(
            status = "success",
            message = "User ${savedUser.username} created successfully",
            data = savedUser
        )
    }

    override fun logIn(userDTO: UserDTO): ApiResponse<String> {
        val email = userDTO.email
        val password = userDTO.password

        authenticatorFactory.authenticate(email, password)

        val user = authRepo.findByEmail(email) ?: throw IllegalArgumentException("User not found")
        val token = jwtUtils.generateToken(user.username, user.id, user.email, user.role)

        return ApiResponse(
            status = "success",
            message = "LogIn Successful",
            data = token
        )
    }

    override fun findByEmail(email: String): User? {
        return authRepo.findByEmail(email)
    }
}