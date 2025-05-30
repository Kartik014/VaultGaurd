package com.example.VaultGuard.DTO

import com.example.VaultGuard.utils.ExtensionFunctions.string
import com.example.VaultGuard.utils.enums.FrequencyCrons.DAILY
import com.example.VaultGuard.utils.enums.StorageType.SUPABASE

data class DatabaseBackupDTO(
    val backupid: String? = "",
    val userid: String? = "",
    val dbid: String? = "",
    val policyname: String? = "",
    val selectedtables: List<String>? = null,
    val frequencycron: String? = DAILY.string(),
    val storagetype: String? = SUPABASE.string(),
    val isactive: Boolean? = true
)
