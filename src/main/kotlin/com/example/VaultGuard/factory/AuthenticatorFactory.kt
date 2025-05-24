package com.example.VaultGuard.factory

import org.springframework.beans.factory.ObjectProvider
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component

@Component
class AuthenticatorFactory(private val authenticationManagerProvider: ObjectProvider<AuthenticationManager>) {
    fun authenticate(email: String, password: String){
        val authenticationManager = authenticationManagerProvider.getIfAvailable() ?: throw IllegalStateException("AuthenticationManager not available")
        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(email, password))
    }
}