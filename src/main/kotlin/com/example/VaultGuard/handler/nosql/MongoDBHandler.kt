package com.example.VaultGuard.handler.nosql

import com.example.VaultGuard.Interfaces.GenericHandlerInterface
import com.example.VaultGuard.models.DatabaseConnection
import com.example.VaultGuard.utils.AESUtils
import com.mongodb.client.MongoClients
import org.springframework.stereotype.Component
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class MongoDatabaseHandler(private val aesUtils: AESUtils): GenericHandlerInterface {
    override fun connectAndFetchData(db: DatabaseConnection): Map<String, Map<String, Any>> {
        val rawPassword = aesUtils.decrypt(db.password)

        val encodedUsername = URLEncoder.encode(db.username, StandardCharsets.UTF_8.toString())
        val encodedPassword = URLEncoder.encode(rawPassword, StandardCharsets.UTF_8.toString())

        val uri = "mongodb+srv://${encodedUsername}:${encodedPassword}@${db.host}/?retryWrites=true&w=majority&appName=Cluster0"

        val client = MongoClients.create(uri)
        val database = client.getDatabase(db.dbname!!)

        val result = mutableMapOf<String, Map<String, Any>>()

        val collections = database.listCollectionNames().toList()

        if (collections.isEmpty()) {
            result["message"] = mapOf("info" to "No collections found in the database!")
        } else {
            for (collection in collections) {
                val docs = database.getCollection(collection).find().limit(100).map { it.toMap() }.toList()
                result[collection] = mapOf("rows" to docs)
            }
        }

        client.close()
        return result
    }
}
