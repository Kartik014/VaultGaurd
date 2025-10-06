package com.example.VaultGuard.DTO

import com.example.VaultGuard.utils.ExtensionFunctions.string
import com.example.VaultGuard.utils.enums.FrequencyCrons.DAILY
import com.example.VaultGuard.utils.enums.StorageType.SUPABASE
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class DatabaseBackupPolicyDTO(
    val policyId: String? = "",

    val userId: String? = "",

    @field:NotBlank(message = "Database ID is required")
    val dbId: String? = "",

    @field:NotBlank(message = "Policy name is required")
    val policyName: String? = "",

    val selectedTables: List<String>? = null,

    val frequencyCron: String? = DAILY.string(),

    val storageType: String? = SUPABASE.string(),

    val isActive: Boolean? = true
)
