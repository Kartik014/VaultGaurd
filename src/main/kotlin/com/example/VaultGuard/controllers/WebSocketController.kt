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

@Component
class WebSocketController(private val objectMapper: ObjectMapper, private val databaseConnectionService: DatabaseConnectionService): TextWebSocketHandler() {

    private val sessions = mutableListOf<WebSocketSession>()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions.add(session)
        session.sendMessage(TextMessage("Connection established"))
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        try {
            val receivedText = objectMapper.readValue(message.payload, FetchTableDTO::class.java)

            val result: ApiResponse<Map<String, Map<String, Any>>> = databaseConnectionService.fetchTableData(receivedText.dbid!!, receivedText.tablename!!)
            val responseJson = objectMapper.writeValueAsString(result)
            session.sendMessage(TextMessage(responseJson))
        } catch (e: Exception) {
            session.sendMessage(TextMessage("Error processing request: ${e.message}"))
        }

    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.remove(session)
    }
}