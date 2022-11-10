package org.dedda.copycat.communication

import org.dedda.copycat.database.Server

class HttpClipboardSink(server: Server): ClipboardSink {

    private val api: ClipboardApi = ClipboardApi(server)

    override suspend fun sendText(text: String): Boolean {
        val push = ClipboardPush.makeText(text)
        val response = api.sendClipboardPush(push)
        return response.success()
    }
}