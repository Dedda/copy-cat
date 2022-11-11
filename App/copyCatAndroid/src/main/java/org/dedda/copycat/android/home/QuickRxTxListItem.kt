package org.dedda.copycat.android.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.dedda.copycat.android.MyApplicationTheme
import org.dedda.copycat.android.R
import org.dedda.copycat.android.appColors
import org.dedda.copycat.android.sampledata.SampleRepository
import org.dedda.copycat.communication.HttpClipboardSink
import org.dedda.copycat.communication.HttpClipboardSource
import org.dedda.copycat.database.Server
import java.net.ConnectException

@Composable
fun QuickRxTxListItem(
    server: Server
) {
    val localContext = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                fontSize = 24.sp,
                text = server.name,
            )
            Row(
                modifier = Modifier.wrapContentSize(),
            ) {
                TextButton(onClick = {
                    sendClipboard(localContext, server)
                }) {
                    Text(
                        color = appColors().sendButtonColor,
                        text = stringResource(R.string.home_send),
                    )
                }
                TextButton(onClick = {
                    receiveClipboard(localContext, server)
                }) {
                    Text(
                        color = appColors().receiveButtonColor,
                        text = stringResource(R.string.home_receive),
                    )
                }
            }
        }
    }
}

fun sendClipboard(context: Context, server: Server) {
    val clipboard = getClipboardManager(context)
    val primaryClip = clipboard.primaryClip
    runBlocking {
        try {
            val toastText = if (primaryClip != null) {
                val text = primaryClip.getItemAt(0).coerceToText(context)
                if (HttpClipboardSink(server).sendText(text.toString())) {
                    "Sent clipboard to ${server.name}"
                } else {
                    "Could not send clipboard to ${server.name}"
                }
            } else {
                "Could not get clipboard contents"
            }
            MainScope().launch {
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            toastNetworkException(e, context, server)
        }
    }
}

fun receiveClipboard(context: Context, server: Server) {
    val clipboard = getClipboardManager(context)
    val source = HttpClipboardSource(server)
    runBlocking {
        try {
            val text = source.requestText()
            val toastText = if (text != null) {
                clipboard.setPrimaryClip(ClipData.newPlainText("CopyCat Paste", text))
                "Received clipboard from ${server.name}"

            } else {
                "Could not request clipboard from ${server.name}"
            }
            MainScope().launch {
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            toastNetworkException(e, context, server)
        }
    }
}

private fun getClipboardManager(context: Context): ClipboardManager {
    return context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
}


fun toastTextFromException(exception: Exception): String {
    return when (exception) {
        is ConnectException -> "Cannot connect: ${exception.localizedMessage}"
        else -> "Unknown error: ${exception.localizedMessage}"
    }
}

private fun toastNetworkException(
    e: Exception,
    context: Context,
    server: Server
) = MainScope().launch {
    val exceptionText = toastTextFromException(e)
    Toast.makeText(context, "[${server.name}] $exceptionText", Toast.LENGTH_SHORT).show()
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun QuickRxTxListItemPreview() {
    MyApplicationTheme {
        QuickRxTxListItem(server = SampleRepository().allServers()[0])
    }
}