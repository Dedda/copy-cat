package org.dedda.copycat.android.sampledata

import org.dedda.copycat.database.Repository
import org.dedda.copycat.database.Server

class SampleRepository: Repository {

    private val servers = mutableListOf(
        Server(1, "Test Server", "test.com"),
        Server(2, "Localhost", "127.0.0.1"),
    )

    override fun allServers(): List<Server> = servers

    override fun serverById(id: Long) = allServers().find { it.id == id }

    override fun serverByAddress(address: String): Server? {
        return allServers().find { it.address == address }
    }

    override fun insertServer(name: String, address: String): Server? {
        if (allServers().none { it.name == name || it.address == address }) {
            val id = if (servers.isEmpty()) 1 else servers.maxOf { it.id } + 1
            val server = Server(id, name, address)
            servers.add(server)
            return server
        }
        return null
    }

    override fun updateServer(server: Server) {
        servers.replaceAll { if (it.id == server.id) server else it }
    }

    override fun deleteServer(id: Long) {
        servers.removeIf { it.id == id }
    }
}