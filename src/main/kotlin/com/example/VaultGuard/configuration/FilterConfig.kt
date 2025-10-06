package com.example.VaultGuard.configuration

import com.example.VaultGuard.filters.RequestKeyValidationFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FilterConfig {

    @Bean
    fun requestKeyValidationFilterRegistration(filter: RequestKeyValidationFilter): FilterRegistrationBean<RequestKeyValidationFilter> {
        val registration = FilterRegistrationBean<RequestKeyValidationFilter>()
        registration.filter = filter
        registration.order = 1
        registration.addUrlPatterns("/*")
        return registration
    }
}