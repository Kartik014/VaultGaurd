package com.example.VaultGuard.utils

object ExtensionFunctions {
    fun <T : Enum<T>> T.string(): String {
        return this.name.lowercase()
    }
}