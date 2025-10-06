package com.example.VaultGuard.configuration

import com.example.VaultGuard.middlewares.JwtAuthFilter
import com.example.VaultGuard.utils.JwtUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.header.writers.CrossOriginEmbedderPolicyHeaderWriter.CrossOriginEmbedderPolicy
import org.springframework.security.web.header.writers.CrossOriginOpenerPolicyHeaderWriter.CrossOriginOpenerPolicy
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.cors.CorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(private val jwtUtils: JwtUtils) {

    @Bean
    fun securityFilterChain(http: HttpSecurity, jwtAuthFilter: JwtAuthFilter): SecurityFilterChain {

        http
            .cors { cors ->
                cors.configurationSource(corsConfigurationSource())
            }
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/auth/**", "/ws/**").permitAll()
                    .anyRequest().authenticated()
            }
            .headers { headers ->
                headers.contentSecurityPolicy {
                    it.policyDirectives(
                        """
                        default-src 'self';
                        script-src 'self' 'unsafe-inline' https:;
                        style-src 'self' 'unsafe-inline' https:;
                        img-src 'self' data:;
                        font-src 'self' https:;
                        connect-src 'self' wss:;
                        frame-ancestors 'none';
                        """.trimIndent()
                    )
                }

                headers.frameOptions { it.deny() }
                headers.xssProtection { it.disable() }
                headers.contentTypeOptions { it.disable() }
                headers.referrerPolicy { it.policy(ReferrerPolicy.SAME_ORIGIN) }
                headers.crossOriginEmbedderPolicy { it.policy(CrossOriginEmbedderPolicy.REQUIRE_CORP) }
                headers.crossOriginOpenerPolicy { it.policy(CrossOriginOpenerPolicy.SAME_ORIGIN) }
            }

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:8080")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        configuration.allowedHeaders = listOf("Authorization", "Content-Type", "X-Requested-With")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun jwtAuthFilter(): JwtAuthFilter = JwtAuthFilter(jwtUtils)
}