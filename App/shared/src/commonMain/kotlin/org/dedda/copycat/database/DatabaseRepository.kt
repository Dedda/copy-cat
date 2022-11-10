package org.dedda.copycat.database

import com.squareup.sqldelight.db.SqlDriver

class DatabaseRepository(sqlDriver: SqlDriver): Repository {

    private val database = AppDatabase(sqlDriver)
    private val query = database.appDatabaseQueries

    constructor(driverFactory: DatabaseDriverFactory): this(driverFactory.createDriver())

    override fun allServers(): List<Server> = query.allServers().executeAsList()

    override fun serverById(id: Long): Server? {
        return query.serverById(id).executeAsOneOrNull()
    }

    override fun serverByAddress(address: String): Server? {
        return query.serverByAddress(address).executeAsOneOrNull()
    }

    override fun insertServer(name: String, address: String): Server? {
        if (query.serverByNameOrAddress(name, address).executeAsOneOrNull() != null) {
            return null
        }
        query.insertServer(name, address)
        return query.serverByNameOrAddress(name, address).executeAsOne()
    }

    override fun updateServer(server: Server) {
        query.updateServer(server.id, server.name, server.address)
    }

    override fun deleteServer(id: Long) {
        query.deleteServer(id)
    }
}