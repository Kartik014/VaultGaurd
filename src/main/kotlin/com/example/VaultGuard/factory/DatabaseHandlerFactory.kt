package com.example.VaultGuard.factory

import com.example.VaultGuard.Interfaces.GenericHandlerInterface
import com.example.VaultGuard.handler.nosql.MongoDatabaseHandler
import com.example.VaultGuard.handler.sql.SqlDatabaseHandler
import com.example.VaultGuard.models.DatabaseConnection
import com.example.VaultGuard.utils.enums.DbNames
import org.springframework.stereotype.Component

@Component
class DatabaseHandlerFactory(private val sqlDatabaseHandler: SqlDatabaseHandler, private val mongoDatabaseHandler: MongoDatabaseHandler) {
    fun getHandler(dbConn: DatabaseConnection): GenericHandlerInterface {
        return when (DbNames.valueOf(dbConn.dbtype!!.uppercase())) {
            DbNames.POSTGRES, DbNames.MYSQL -> sqlDatabaseHandler
            DbNames.MONGODB -> mongoDatabaseHandler
        }
    }
}