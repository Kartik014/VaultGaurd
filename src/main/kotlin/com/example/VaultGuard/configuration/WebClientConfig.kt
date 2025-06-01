package com.example.VaultGuard.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {

    @Value("\${SUPABASE_SERVICE_ROLE_KEY}")
    private lateinit var supabaseServiceRoleKey: String

    @Bean
    fun supabaseWebClient(builder: WebClient.Builder): WebClient {
        return builder
            .baseUrl("https://jyrlcsjpkssmoliyhfcz.supabase.co/storage/v1/")
            .defaultHeader("Authorization", "Bearer $supabaseServiceRoleKey")
            .build()
    }
}