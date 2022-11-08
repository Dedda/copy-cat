package org.dedda.copycat.communication

import io.ktor.resources.Resource
import io.ktor.util.decodeBase64String
import io.ktor.util.encodeBase64
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private const val PROTOCOL_VERSION: Int = 1

@Serializable
@Resource("/request")
data class ClipboardRequest(
    @SerialName("version")
    val version: Int = PROTOCOL_VERSION,
    @SerialName("clipboard_type")
    val clipboardType: String,
) {
    companion object {
        fun text() = ClipboardRequest(clipboardType = ClipboardType.TEXT.value)
    }
}

@Serializable
data class ClipboardPullResponse(
    @SerialName("version")
    val version: Int,
    @SerialName("clipboard_type")
    val clipboardType: String,
    @SerialName("contents")
    val contentsBase64: String,
    @SerialName("error")
    val error: String?,
) {
    fun getText(): String? = if (error == null && clipboardType == "text") {
        contentsBase64.decodeBase64String()
    } else {
        null
    }
}

@Serializable
@Resource("/push")
data class ClipboardPush(
    @SerialName("version")
    val version: Int = PROTOCOL_VERSION,
    @SerialName("clipboard_type")
    val clipboardType: String,
    @SerialName("contents")
    val contentsBase64: String,
) {
    companion object {
        fun makeText(data: String): ClipboardPush {
            val contentsBase64 = data.encodeBase64()
            return ClipboardPush(
                clipboardType = ClipboardType.TEXT.value,
                contentsBase64 = contentsBase64,
            )
        }
    }
}

@Serializable
data class ClipboardPushResponse(
    @SerialName("version")
    val version: Int,
    @SerialName("error")
    val error: String?,
) {
    fun success() = error == null
}

enum class ClipboardType(val value: String) {
    TEXT("text")
}