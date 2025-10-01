package com.example.VaultGuard.controllers

import com.example.VaultGuard.DTO.AddRowDataDTO
import com.example.VaultGuard.DTO.BaseSocketDTO
import com.example.VaultGuard.DTO.DbUpdateEvent
import com.example.VaultGuard.DTO.EditTableDTO
import com.example.VaultGuard.DTO.FetchTableDTO
import com.example.VaultGuard.DTO.PageLimitDTO
import com.example.VaultGuard.DTO.RemoveRowDataDTO
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
    private val userPageLimitMap = ConcurrentHashMap<String, ConcurrentHashMap<String, PageLimitDTO>>()

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
                    val dbId = receivedText.dbId ?: throw IllegalArgumentException("Database ID is required")
                    val tableName = receivedText.tableName ?: throw IllegalArgumentException("Table name is required")
                    val tableKey = "$dbId:$tableName"
                    val page = receivedText.page
                    val limit = receivedText.limit

                    userPageLimitMap.computeIfAbsent(userId) { ConcurrentHashMap() }[tableKey] = PageLimitDTO(page, limit)

                    userTableViewMap[userId]?.let { oldTableKey ->
                        tableUserViewMap[oldTableKey]?.remove(userId)
                        if (tableUserViewMap[oldTableKey]?.isEmpty() == true) {
                            tableUserViewMap.remove(oldTableKey)
                        }
                    }

                    userTableViewMap[userId] = tableKey

                    val fetchTableDTO = FetchTableDTO(
                        dbId = dbId,
                        tableName = tableName,
                        page = userPageLimitMap[userId]?.get(tableKey)!!.page,
                        limit = userPageLimitMap[userId]?.get(tableKey)!!.limit
                    )

                    tableUserViewMap.computeIfAbsent(tableKey) { mutableSetOf() }.add(userId)

                    databaseConnectionService.fetchTableData(userId, fetchTableDTO)
                }

                "edit_data" -> {
                    val receivedText = objectMapper.readValue(message.payload, EditTableDTO::class.java)
                    val dbId = receivedText.dbId
                    val tableName = receivedText.tableName
                    val rowIdentifier = receivedText.rowIdentifier
                    val columnUpdates = receivedText.columnUpdates

                    val editTableDTO = EditTableDTO(
                        type = "edit_data",
                        dbId = dbId,
                        tableName = tableName,
                        rowIdentifier = rowIdentifier,
                        columnUpdates = columnUpdates
                    )

                    val response = databaseConnectionService.editDbData(editTableDTO)
                    sendToUser(userId, response)

                    databaseConnectionService.fetchEditedData(userId, editTableDTO)
                }

                "add_data" -> {
                    val receivedText = objectMapper.readValue(message.payload, AddRowDataDTO::class.java)
                    val dbId = receivedText.dbId
                    val tableName = receivedText.tableName
                    val newData = receivedText.newData

                    val addRowDataDTO = AddRowDataDTO(
                        type = "add_data",
                        dbId = dbId,
                        tableName = tableName,
                        newData = newData
                    )

                    val response = databaseConnectionService.addDataToDB(addRowDataDTO)
                    sendToUser(userId, response)
                }

                "remove_data" -> {
                    val receivedText = objectMapper.readValue(message.payload, RemoveRowDataDTO::class.java)
                    val dbId = receivedText.dbId
                    val tableName = receivedText.tableName
                    val removeDataKey = receivedText.removeDataKey

                    val removeRowDataDTO = RemoveRowDataDTO(
                        type = "remove_data",
                        dbId = dbId,
                        tableName = tableName,
                        removeDataKey = removeDataKey
                    )

                    val response = databaseConnectionService.removeDataFromDB(removeRowDataDTO)
                    sendToUser(userId, response)
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

            if (tableKey != null) {
                tableUserViewMap[tableKey]?.remove(userId)
                if (tableUserViewMap[tableKey]?.isEmpty() == true) {
                    tableUserViewMap.remove(tableKey)
                }
            }

            userPageLimitMap.remove(userId)
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

    fun broadcastTableUpdate(tableName: String, dbId: String, data: Any) {
        val tableKey = "$dbId:$tableName"

        tableUserViewMap[tableKey]?.forEach { userId ->
            val pageLimit = userPageLimitMap[userId]?.get(tableKey)

            if (pageLimit != null) {
                val fetchTableDTO = FetchTableDTO(
                    dbId = dbId,
                    tableName = tableName,
                    page = pageLimit.page,
                    limit = pageLimit.limit
                )

                val userData = databaseConnectionService.databaseConnectionRepo.fetchTableData(fetchTableDTO)

                val response = mapOf(
                    "type" to "db_update",
                    "tablename" to tableName,
                    "data" to userData,
                    "page" to pageLimit.page,
                    "limit" to pageLimit.limit,
                    "timestamp" to System.currentTimeMillis()
                )

                sendToUser(userId, response)
            }
        }
    }

    @EventListener
    fun handleDbUpdateEvent(event: DbUpdateEvent) {
        broadcastTableUpdate(event.tableName, event.dbId, event.data)
    }
}