package com.example.VaultGuard.repository

import com.example.VaultGuard.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthRepo: JpaRepository<User, String> {

    fun findByEmail(email: String): User?

    override fun count(): Long
}