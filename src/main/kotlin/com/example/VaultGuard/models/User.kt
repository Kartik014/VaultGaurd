package com.example.VaultGuard.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "users")
data class User (
    @Id
    @Column(unique = true, nullable = false)
    val id: String = UUID.randomUUID().toString(),

    @Column(nullable = false, unique = false)
    val username: String = "",

    @Column(nullable = false, unique = true)
    val email: String = "",

    @Column(nullable = false)
    val password: String = "",

    @Column(nullable = false, unique = false)
    val role: String = "",

    @Column(nullable = false, unique = false)
    val createdby: String = "",

    @Column(nullable = false, unique = false)
    val mustchangepassword: Boolean = false
)