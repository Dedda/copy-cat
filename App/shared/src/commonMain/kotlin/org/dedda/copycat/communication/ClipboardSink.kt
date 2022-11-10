package org.dedda.copycat.communication

interface ClipboardSink {

    suspend fun sendText(text: String): Boolean

}