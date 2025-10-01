package com.example.VaultGuard.services

import com.example.VaultGuard.DTO.RoleDTO
import com.example.VaultGuard.DTO.UserDTO
import com.example.VaultGuard.Interfaces.SuperAdminInterface
import com.example.VaultGuard.factory.UserFactory
import com.example.VaultGuard.models.ApiResponse
import com.example.VaultGuard.models.User
import com.example.VaultGuard.repository.SuperAdminRepo
import com.example.VaultGuard.utils.JwtUtils
import com.example.VaultGuard.utils.enums.UserRoles
import org.springframework.stereotype.Service

@Service
class SuperAdminService(private val superAdminRepo: SuperAdminRepo, private val userFactory: UserFactory, private val jwtUtils: JwtUtils): SuperAdminInterface {

    override fun addUser(userDTO: UserDTO): ApiResponse<String> {
        if(superAdminRepo.findByEmail(userDTO.email) != null) {
            throw IllegalArgumentException("User already registered")
        }

        userDTO.createdBy = jwtUtils.getCurrentUserId()
        userDTO.mustChangePassword = true

        val newUser: User = userFactory.createUser(userDTO)
        val savedUser: User = superAdminRepo.save(newUser)

        return ApiResponse(
            status = "success",
            message = "User created successfully",
            data = "User ${savedUser.username} with ${savedUser.role} role created successfully"
        )
    }

    override fun getAllUsers(): ApiResponse<List<User>> {
        val userList = superAdminRepo.findAll()

        return ApiResponse(
            status = "success",
            message = "users fetched successfully",
            data = userList
        )
    }

    override fun updateRole(roleDTO: RoleDTO): ApiResponse<User> {
        val existingUser = superAdminRepo.getUserById(roleDTO.userId!!) ?: throw IllegalArgumentException("No user found")
        val validRole = try {
            UserRoles.valueOf(roleDTO.newRole!!.uppercase())
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid role: ${roleDTO.newRole}")
        }

        existingUser.role = validRole.name.lowercase()

        val updatedUser = superAdminRepo.save(existingUser)

        return ApiResponse(
            status = "success",
            message = "user role updated successfully",
            data = updatedUser
        )
    }

    override fun removeUser(userId: String): ApiResponse<List<User>> {
        val userExisted = superAdminRepo.getUserById(userId) ?: throw IllegalArgumentException("User not found")

        superAdminRepo.deleteById(userId)
        val updatedUserList = superAdminRepo.findAll()

        return ApiResponse(
            status = "success",
            message = "User removed successfully",
            data = updatedUserList
        )
    }
}