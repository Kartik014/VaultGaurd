package com.example.VaultGuard.handler.nosql

import com.example.VaultGuard.DTO.AddRowDataDTO
import com.example.VaultGuard.DTO.EditTableDTO
import com.example.VaultGuard.DTO.FetchTableDTO
import com.example.VaultGuard.DTO.RemoveRowDataDTO
import com.example.VaultGuard.Interfaces.GenericHandlerInterface
import com.example.VaultGuard.factory.DatabaseConnectionFactory
import com.example.VaultGuard.models.DatabaseConnection
import com.mongodb.client.MongoClients
import org.bson.Document
import org.springframework.stereotype.Component

@Component
class MongoDatabaseHandler(private val databaseConnectionFactory: DatabaseConnectionFactory): GenericHandlerInterface {
    override fun connectAndFetchData(db: DatabaseConnection, fetchTableDTO: FetchTableDTO): Map<String, Map<String, Any>> {
        val uri = databaseConnectionFactory.connectMongoDb(db)

        val client = MongoClients.create(uri)
        val database = client.getDatabase(db.dbname!!)

        val result = mutableMapOf<String, Map<String, Any>>()

        val collectionNames = database.listCollectionNames().toList()

        val tableName = fetchTableDTO.tableName.toString()
        val page = fetchTableDTO.page
        val limit = fetchTableDTO.limit
        val offset = page * limit

        if (collectionNames.isEmpty()) {
            result["message"] = mapOf("info" to "No collections found in the database!")
        } else if (!collectionNames.contains(tableName)) {
            result["message"] = mapOf("info" to "Collection '$tableName' not found in the database!")
        } else {
            val collection = database.getCollection(tableName)
            val totalDocs = collection.countDocuments()
            val docs = collection.find().skip(offset).limit(limit).map { it.toMap() }.toList()

            result[tableName] = mapOf("rows" to docs)
            result["pagination"] = mapOf(
                "totalRows" to totalDocs,
                "page" to page,
                "limit" to limit,
                "hasPrevious" to (page > 0),
                "hasNext" to ((offset + limit) < totalDocs)
            )
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
        val collection = database.getCollection(editTableDTO.tableName)

        val filter = Document(editTableDTO.rowIdentifier)
        val update = Document("\$set", Document(editTableDTO.columnUpdates).toString())

        val result = collection.updateMany(filter, update)

        client.close()

        return result.modifiedCount.toInt()
    }

    override fun addDataToDB(db: DatabaseConnection, addRowDataDTO: AddRowDataDTO): Boolean {
        val uri = databaseConnectionFactory.connectMongoDb(db)

        val client = MongoClients.create(uri)
        val database = client.getDatabase(db.dbname!!)
        val collection = database.getCollection(addRowDataDTO.tableName)

        val doc = Document(addRowDataDTO.newData)

        val result = collection.insertOne(doc)

        client.close()

        return true
    }

    override fun removeDataFromDB(db: DatabaseConnection, removeRowDataDTO: RemoveRowDataDTO): Boolean {
        val uri = databaseConnectionFactory.connectMongoDb(db)

        val client = MongoClients.create(uri)
        val database = client.getDatabase(db.dbname!!)
        val collection = database.getCollection(removeRowDataDTO.tableName)

        val filter = Document(removeRowDataDTO.removeDataKey.columnName, removeRowDataDTO.removeDataKey.columnValue)

        val result = collection.deleteOne(filter)

        client.close()

        return true
    }

    override fun fetchEditedData(db: DatabaseConnection, editTableDTO: EditTableDTO): Map<String, Any> {
        val uri = databaseConnectionFactory.connectMongoDb(db)

        val client = MongoClients.create(uri)
        val database = client.getDatabase(db.dbname!!)
        val collection = database.getCollection(editTableDTO.tableName)

        val filter = Document(editTableDTO.rowIdentifier)
        val result = collection.find(filter).firstOrNull()

        client.close()

        return result?.toMap() ?: emptyMap()
    }
}
