package com.example.VaultGuard.DTO

import com.example.VaultGuard.utils.ExtensionFunctions.string
import com.example.VaultGuard.utils.enums.FrequencyCrons.DAILY
import com.example.VaultGuard.utils.enums.StorageType.SUPABASE
import com.example.VaultGuard.validators.CreateBackupPolicyValidator
import com.example.VaultGuard.validators.CreateBackupValidator
import jakarta.validation.constraints.NotBlank

data class DatabaseBackupPolicyDTO(
    @field:NotBlank(message = "policy ID is required", groups = [CreateBackupValidator::class])
    val policyId: String? = "",

    val userId: String? = "",

    @field:NotBlank(message = "Database ID is required", groups = [CreateBackupPolicyValidator::class])
    val dbId: String? = "",

    @field:NotBlank(message = "Policy name is required", groups = [CreateBackupPolicyValidator::class])
    val policyName: String? = "",

    val selectedTables: List<String>? = null,

    val frequencyCron: String? = DAILY.string(),

    val storageType: String? = SUPABASE.string(),

    val isActive: Boolean? = true
)
