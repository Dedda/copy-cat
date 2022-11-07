package org.dedda.copycat.database

interface Repository {

    fun allServers(): List<Server>

    fun insertServer(name: String, address: String): Server?

}