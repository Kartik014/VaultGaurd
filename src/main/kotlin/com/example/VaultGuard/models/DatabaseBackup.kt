package com.example.VaultGuard.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "databasebackup")
data class DatabaseBackup(
    @Id
    @Column(nullable = false, unique = true)
    val backupid: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    val user: User = User(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dbid", nullable = false)
    val databaseConnection: DatabaseConnection = DatabaseConnection(),

    @Column(nullable = false, unique = false)
    val policyname: String? = "",

    @Column(nullable = false, unique = false)
    val selectedtables: String? = "all",

    @Column(nullable = false)
    val frequencycron: String = "daily",

    @Column(nullable = false)
    val storagetype: String = "supabase",

    @Column(nullable = false)
    val isactive: Boolean = true
)
