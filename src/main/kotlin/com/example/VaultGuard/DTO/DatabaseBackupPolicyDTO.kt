package com.example.VaultGuard.DTO

import com.example.VaultGuard.utils.ExtensionFunctions.string
import com.example.VaultGuard.utils.enums.FrequencyCrons.DAILY
import com.example.VaultGuard.utils.enums.StorageType.SUPABASE

data class DatabaseBackupPolicyDTO(
    val policyId: String? = "",
    val userId: String? = "",
    val dbId: String? = "",
    val policyName: String? = "",
    val selectedTables: List<String>? = null,
    val frequencyCron: String? = DAILY.string(),
    val storageType: String? = SUPABASE.string(),
    val isActive: Boolean? = true
)
