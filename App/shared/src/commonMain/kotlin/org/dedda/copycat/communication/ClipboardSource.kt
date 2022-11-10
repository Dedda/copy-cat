package org.dedda.copycat.communication

interface ClipboardSource {

    suspend fun requestText(): String?

}