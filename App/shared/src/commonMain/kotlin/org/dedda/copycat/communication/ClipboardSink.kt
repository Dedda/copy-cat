package org.dedda.copycat.communication

interface ClipboardSink {

    fun sendText(text: String): Boolean

}