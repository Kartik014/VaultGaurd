package com.example.VaultGuard.configuration

import com.example.VaultGuard.controllers.WebSocketController
import com.example.VaultGuard.utils.JwtHandshakeInterceptor
import com.example.VaultGuard.utils.JwtUtils
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(private val webSocketController: WebSocketController, private val jwtUtils: JwtUtils): WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(webSocketController, "/ws").setAllowedOrigins("*")
            .addInterceptors(JwtHandshakeInterceptor(jwtUtils))
    }

}