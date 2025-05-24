package com.example.VaultGuard.configuration

import com.example.VaultGuard.Interfaces.AuthServiceInterface
import com.example.VaultGuard.utils.CustomAuthenticationProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class AuthConfig(private val authService: AuthServiceInterface) {

    @Bean
    fun authenticationManager(passwordEncoder: PasswordEncoder): AuthenticationManager {
        return ProviderManager(
            CustomAuthenticationProvider(authService, passwordEncoder)
        )
    }
}