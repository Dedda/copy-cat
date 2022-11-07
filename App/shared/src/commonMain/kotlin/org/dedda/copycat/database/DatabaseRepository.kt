package org.dedda.copycat.database

import com.squareup.sqldelight.db.SqlDriver

class DatabaseRepository(sqlDriver: SqlDriver): Repository {

    private val database = AppDatabase(sqlDriver)
    private val query = database.appDatabaseQueries

    constructor(driverFactory: DatabaseDriverFactory): this(driverFactory.createDriver())

    override fun allServers(): List<Server> = query.allServers().executeAsList()

    override fun insertServer(name: String, address: String): Server? {
        if (query.serverByNameOrAddress(name, address).executeAsOneOrNull() != null) {
            return null
        }
        query.insertServer(name, address)
        return query.serverByNameOrAddress(name, address).executeAsOne()
    }
}