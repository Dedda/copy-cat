package org.dedda.copycat.communication

interface ClipboardSource {

    fun requestText(): String?

}