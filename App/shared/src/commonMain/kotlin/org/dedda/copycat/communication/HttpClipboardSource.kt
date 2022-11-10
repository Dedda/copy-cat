package org.dedda.copycat.communication

import org.dedda.copycat.database.Server

class HttpClipboardSource(private val server: Server): ClipboardSource {

    private val api: ClipboardApi = ClipboardApi(server)

    override suspend fun requestText(): String? {
        val request = ClipboardRequest.text()
        val response = api.sendClipboardRequest(request)
        return response.getText()
    }
}