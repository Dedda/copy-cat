package org.dedda.copycat.database

interface Repository {

    fun allServers(): List<Server>

    fun serverById(id: Long): Server?

    fun insertServer(name: String, address: String): Server?

    fun updateServer(server: Server)

    fun deleteServer(id: Long)

}