package com.example.VaultGuard.handler.nosql

import com.example.VaultGuard.DTO.EditTableDTO
import com.example.VaultGuard.Interfaces.GenericHandlerInterface
import com.example.VaultGuard.factory.DatabaseConnectionFactory
import com.example.VaultGuard.models.DatabaseConnection
import com.mongodb.client.MongoClients
import org.bson.Document
import org.springframework.stereotype.Component

@Component
class MongoDatabaseHandler(private val databaseConnectionFactory: DatabaseConnectionFactory): GenericHandlerInterface {
    override fun connectAndFetchData(db: DatabaseConnection, tablename: String): Map<String, Map<String, Any>> {
        val uri = databaseConnectionFactory.connectMongoDb(db)
        val client = MongoClients.create(uri)
        val database = client.getDatabase(db.dbname!!)

        val result = mutableMapOf<String, Map<String, Any>>()

        val collectionNames = database.listCollectionNames().toList()
        if (collectionNames.isEmpty()) {
            result["message"] = mapOf("info" to "No collections found in the database!")
        } else if (!collectionNames.contains(tablename)) {
            result["message"] = mapOf("info" to "Collection '$tablename' not found in the database!")
        } else {
            val docs = database.getCollection(tablename).find().limit(100).map { it.toMap() }.toList()
            result[tablename] = mapOf("rows" to docs)
        }

        client.close()
        return result
    }

    override fun fetchTableNames(db: DatabaseConnection): List<String> {
        val uri = databaseConnectionFactory.connectMongoDb(db)

        val client = MongoClients.create(uri)
        val database = client.getDatabase(db.dbname!!)

        val collections = database.listCollectionNames().toList()

        client.close()
        return collections
    }

    override fun editDbData(db: DatabaseConnection, editTableDTO: EditTableDTO): Int {
        val uri = databaseConnectionFactory.connectMongoDb(db)

        val client = MongoClients.create(uri)
        val database = client.getDatabase(db.dbname!!)
        val collection = database.getCollection(editTableDTO.tablename)

        val filter = Document(editTableDTO.rowidentifier)
        val update = Document("\$set", Document(editTableDTO.columnupdates).toString())

        val result = collection.updateMany(filter, update)
        return result.modifiedCount.toInt()
    }

    override fun fetchEditedData(db: DatabaseConnection, editTableDTO: EditTableDTO): Map<String, Any> {
        val uri = databaseConnectionFactory.connectMongoDb(db)

        val client = MongoClients.create(uri)
        val database = client.getDatabase(db.dbname!!)
        val collection = database.getCollection(editTableDTO.tablename)

        val filter = Document(editTableDTO.rowidentifier)
        val result = collection.find(filter).firstOrNull()

        return result?.toMap() ?: emptyMap()
    }
}
