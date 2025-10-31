package com.example.VaultGuard.controllers

import com.example.VaultGuard.DTO.RoleDTO
import com.example.VaultGuard.DTO.UserDTO
import com.example.VaultGuard.Interfaces.SuperAdminInterface
import com.example.VaultGuard.models.ApiResponse
import com.example.VaultGuard.models.User
import com.example.VaultGuard.validators.AddUserValidator
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/superadmin")
@PreAuthorize("hasAuthority('superadmin')")
class SuperAdminController(private val superAdminService: SuperAdminInterface) {

    @PostMapping("/addUser")
    fun addUser(@Validated(AddUserValidator::class) @RequestBody userDTO: UserDTO): ResponseEntity<ApiResponse<String>>{
        return try {
            val newUser = superAdminService.addUser(userDTO)
            ResponseEntity(newUser, HttpStatus.OK)
        } catch (e: IllegalArgumentException){
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }

    @GetMapping("/allUsers")
    fun getAllUsers(): ResponseEntity<ApiResponse<List<User>>>{
        return try {
            val usersList = superAdminService.getAllUsers()
            ResponseEntity(usersList, HttpStatus.OK)
        } catch (e: IllegalArgumentException){
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }

    @PatchMapping("/updateRole")
    fun updateUserRole(@RequestBody roleDTO: RoleDTO): ResponseEntity<ApiResponse<User>>{
        return try {
            val updatedUserRole = superAdminService.updateRole(roleDTO)
            ResponseEntity(updatedUserRole, HttpStatus.OK)
        } catch (e: IllegalArgumentException){
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }

    @DeleteMapping("/removeUser")
    fun removeUser(@RequestBody userId: Map<String, String>): ResponseEntity<ApiResponse<List<User>>>{
        if(userId["userId"]?.trim()?.isBlank() == true){
            throw IllegalArgumentException("UserId not found")
        }
        return try {
            val updatedUserList = superAdminService.removeUser(userId["userId"].toString())
            ResponseEntity(updatedUserList, HttpStatus.OK)
        } catch (e: IllegalArgumentException){
            throw IllegalArgumentException(e.message)
        } catch (e: Exception) {
            throw Exception("Internal Server Error")
        }
    }
}