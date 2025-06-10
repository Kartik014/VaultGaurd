package com.example.VaultGuard.controllers

import com.example.VaultGuard.DTO.FetchTableDTO
import com.example.VaultGuard.models.ApiResponse
import com.example.VaultGuard.services.DatabaseConnectionService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap

@Component
class WebSocketController(private val objectMapper: ObjectMapper, private val databaseConnectionService: DatabaseConnectionService): TextWebSocketHandler() {

    private val sessions = mutableSetOf<WebSocketSession>()
    private val userSessions = ConcurrentHashMap<String, WebSocketSession>()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val userId = session.attributes["userId"] as? String
        val username = session.attributes["username"] as? String
        if (userId != null) {
            userSessions[userId] = session
            sessions.add(session)

            val welcomeMessage = mapOf(
                "type" to "connection",
                "message" to "Connection established for user: $username",
                "userId" to userId,
                "timestamp" to System.currentTimeMillis()
            )

            sendToUser(userId, welcomeMessage)
        } else {
            session.close(CloseStatus.BAD_DATA.withReason("No user information found"))
        }
        sessions.add(session)
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        try {
            val userId = session.attributes["userId"] as? String

            if (userId == null) {
                val errorResponse = mapOf(
                    "type" to "error",
                    "message" to "User not authenticated",
                    "timestamp" to System.currentTimeMillis()
                )
                sendToUser(userId.toString(), errorResponse)
                return
            }

            val receivedText = objectMapper.readValue(message.payload, FetchTableDTO::class.java)

            val result: ApiResponse<Map<String, Map<String, Any>>> = databaseConnectionService.fetchTableData(receivedText.dbid!!, receivedText.tablename!!)
            sendToUser(userId, result)
        } catch (e: Exception) {
            session.sendMessage(TextMessage("Error processing request: ${e.message}"))
        }

    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val userId = session.attributes["userId"] as? String
        if (userId != null) {
            userSessions.remove(userId)
        }
        sessions.remove(session)
    }

    fun sendToUser(userId: String, data: Any) {
        try {
            val session = userSessions[userId]
            if (session != null && session.isOpen) {
                val jsonMessage = data as? String ?: objectMapper.writeValueAsString(data)
                session.sendMessage(TextMessage(jsonMessage))
            } else {
                println("User $userId not connected or session closed")
            }
        } catch (e: Exception) {
            println("Error sending message to user $userId: ${e.message}")
        }
    }
}