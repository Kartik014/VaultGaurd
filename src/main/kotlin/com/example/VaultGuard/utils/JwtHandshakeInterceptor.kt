package com.example.VaultGuard.utils

import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import java.lang.Exception

class JwtHandshakeInterceptor(private val jwtUtils: JwtUtils): HandshakeInterceptor {
    override fun beforeHandshake(request: ServerHttpRequest, response: ServerHttpResponse, wsHandler: WebSocketHandler, attributes: MutableMap<String, Any>): Boolean {
        try {
            val token = request.headers.getFirst("Authorization")!!.split(" ")[1]
            if (token.isBlank()) {
                println("WebSocket connection rejected: No token provided")
                response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED)
                return false
            }
            val userId = jwtUtils.extractIdFromClaim(token) as? String

            if (userId.isNullOrBlank()) {
                println("WebSocket connection rejected: Invalid token format")
                response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED)
                return false
            }

            if (!jwtUtils.validateToken(token, userId)) {
                println("WebSocket connection rejected: Token validation failed")
                response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED)
                return false
            }

            val username = jwtUtils.extractUsername(token) as? String ?: "unknown"
            val email = jwtUtils.extractEmail(token) as? String ?: "unknown"
            val role = jwtUtils.extractUserRole(token) as? String ?: "USER"

            attributes["userId"] = userId
            attributes["username"] = username
            attributes["email"] = email
            attributes["role"] = role
            attributes["token"] = token

            return true
        } catch (e: Exception) {
            response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED)
            return false
        }
    }

    override fun afterHandshake(request: ServerHttpRequest, response: ServerHttpResponse, wsHandler: WebSocketHandler, exception: Exception?) {
        if (exception == null) {
            println("WebSocket handshake completed successfully")
        } else {
            println("WebSocket handshake failed: ${exception.message}")
        }
    }
}