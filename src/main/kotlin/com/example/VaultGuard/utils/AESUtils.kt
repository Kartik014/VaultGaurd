package com.example.VaultGuard.utils

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@Component
class AESUtils(
    @Value("\${encryption.secret}") private val SECRET: String
) {
    fun encrypt(input: String): String {
        val key = SecretKeySpec(SECRET.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return Base64.getEncoder().encodeToString(cipher.doFinal(input.toByteArray()))
    }

    fun decrypt(encrypted: String): String {
        val key = SecretKeySpec(SECRET.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, key)
        return String(cipher.doFinal(Base64.getDecoder().decode(encrypted)))
    }
}