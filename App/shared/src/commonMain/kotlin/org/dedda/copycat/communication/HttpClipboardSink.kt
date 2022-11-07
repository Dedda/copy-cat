package org.dedda.copycat.communication

import org.dedda.copycat.database.Server

class HttpClipboardSink(private val server: Server): ClipboardSink {
    override fun sendText(text: String): Boolean {
        return false
    }
}