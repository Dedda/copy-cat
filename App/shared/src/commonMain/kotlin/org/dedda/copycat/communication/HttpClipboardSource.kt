package org.dedda.copycat.communication

import org.dedda.copycat.database.Server

class HttpClipboardSource(private val server: Server): ClipboardSource {

    override fun requestText(): String? {
        print("Requesting clipboard text from `${server.name}` (${server.address})")
        val request = ClipboardRequest.text()
        return null
    }
}