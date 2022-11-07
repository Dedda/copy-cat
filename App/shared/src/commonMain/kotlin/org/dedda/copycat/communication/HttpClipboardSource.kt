package org.dedda.copycat.communication

import org.dedda.copycat.database.Server

class HttpClipboardSource(private val server: Server): ClipboardSource {
    override fun receiveText(): String? {
        return null
    }
}