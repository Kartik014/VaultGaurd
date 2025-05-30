package com.example.VaultGuard.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {
    @Bean
    fun supabaseWebClient(builder: WebClient.Builder): WebClient {
        return builder
            .baseUrl("https://jyrlcsjpkssmoliyhfcz.supabase.co/storage/v1/")
            .defaultHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imp5cmxjc2pwa3NzbW9saXloZmN6Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0ODEwNDE3NSwiZXhwIjoyMDYzNjgwMTc1fQ.55QLQoOoDmdtkD-CcKB3iMxdxYgcgnqHN7NKz-T2owo")
            .build()
    }
}