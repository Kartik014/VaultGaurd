package com.example.VaultGuard.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "databaseconnection")
data class DatabaseConnection(

    @Id
    @Column(unique = true, nullable = false)
    var dbid: String? = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    val user: User = User(),

    @Column(unique = false, nullable = false)
    val dbtype: String? = "",

    @Column(unique = false, nullable = false)
    val host: String? = "",

    @Column(unique = false, nullable = false)
    val port: Int? = null,

    @Column(unique = false, nullable = false)
    val dbname: String? = "",

    @Column(nullable = false, unique = false)
    val username: String? = "",

    @Column(nullable = false, unique = false)
    val password: String = "",

    @Column(nullable = false, unique = false)
    val ssl: Boolean = false
)
