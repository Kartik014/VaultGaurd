package com.example.VaultGuard.controllers

import com.example.VaultGuard.DTO.BaseSocketDTO
import com.example.VaultGuard.DTO.DbUpdateEvent
import com.example.VaultGuard.DTO.EditTableDTO
import com.example.VaultGuard.DTO.FetchTableDTO
import com.example.VaultGuard.services.DatabaseConnectionService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.event.EventListener
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
    private val userTableViewMap = ConcurrentHashMap<String, String>()
    private val tableUserViewMap = ConcurrentHashMap<String, MutableSet<String>>()

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

        try {
            val base = objectMapper.readValue(message.payload, BaseSocketDTO::class.java)

            when (val type = base.type) {
                "fetch_table" -> {
                    val receivedText = objectMapper.readValue(message.payload, FetchTableDTO::class.java)
                    val dbid = receivedText.dbid ?: throw IllegalArgumentException("Database ID is required")
                    val tablename = receivedText.tablename ?: throw IllegalArgumentException("Table name is required")
                    val tableKey = "$dbid:$tablename"
                    if(dbid != null && tablename != null) {
                        userTableViewMap[userId]?.let { oldTableKey ->
                            tableUserViewMap[oldTableKey]?.remove(userId)
                            if(tableUserViewMap[oldTableKey]?.isEmpty() == true) {
                                tableUserViewMap.remove(oldTableKey)
                            }
                        }

                        userTableViewMap[userId] = tableKey
                        tableUserViewMap.computeIfAbsent(tableKey) { mutableSetOf() }.add(userId)

                        databaseConnectionService.fetchTableData(userId, dbid, tablename)
                    } else {
                        throw IllegalArgumentException("Invalid database ID or table name")
                    }
                }
                "edit_data" -> {
                    val receivedText = objectMapper.readValue(message.payload, EditTableDTO::class.java)
                    val dbid = receivedText.dbid
                    val tablename = receivedText.tablename
                    val rowidentifier = receivedText.rowidentifier
                    val columnupdates = receivedText.columnupdates

                    val editTableDTO = EditTableDTO(
                        type = "edit_data",
                        dbid = dbid,
                        tablename = tablename,
                        rowidentifier = rowidentifier,
                        columnupdates = columnupdates
                    )

                    val response = databaseConnectionService.editDbData(editTableDTO)
                    sendToUser(userId, response)

                    databaseConnectionService.fetchEditedData(userId, editTableDTO)
                }
                else -> {
                    session.sendMessage(TextMessage("Unknown message type: $type"))
                }
            }
        } catch (e: Exception) {
            session.sendMessage(TextMessage("Error processing request: ${e.message}"))
        }

    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val userId = session.attributes["userId"] as? String
        if (userId != null) {
            val tableKey = userTableViewMap.remove(userId)

            if(tableKey != null) {
                tableUserViewMap[tableKey]?.remove(userId)
                if (tableUserViewMap[tableKey]?.isEmpty() == true) {
                    tableUserViewMap.remove(tableKey)
                }
            }
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

    fun broadcastTableUpdate(tablename: String, dbid: String, data: Any) {
        val response = mapOf(
                "type" to "db_update",
                "tablename" to tablename,
                "data" to data,
                "timestamp" to System.currentTimeMillis()
            )

        val tableKey = "$dbid:$tablename"

        tableUserViewMap[tableKey]?.forEach { userId ->
            sendToUser(userId, response)
        }
    }

    @EventListener
    fun handleDbUpdateEvent(event: DbUpdateEvent) {
        broadcastTableUpdate(event.tablename, event.dbId, event.data)
    }
}