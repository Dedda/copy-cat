package org.dedda.copycat.communication

interface ClipboardSource {

    fun receiveText(): String?

}