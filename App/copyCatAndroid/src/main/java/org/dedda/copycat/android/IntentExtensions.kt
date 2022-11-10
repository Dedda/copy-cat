package org.dedda.copycat.android

import android.content.Intent

fun Intent.getAddServerAddress(): String? {
    if (action != Intent.ACTION_VIEW) {
        return null
    }
    val uri = data ?: return null
    if (uri.scheme != "copycat" || uri.host != "connect.app") {
        return null
    }
    return uri.getQueryParameter("address")
}

fun Intent.withAddServerAddressDo(block: (String) -> Unit) {
    getAddServerAddress()?.let {
        block(it)
    }
}