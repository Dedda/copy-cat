package org.dedda.copycat.android

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.dedda.copycat.android.sampledata.SampleRepository
import org.dedda.copycat.communication.HttpClipboardSink
import org.dedda.copycat.communication.HttpClipboardSource
import org.dedda.copycat.database.Repository
import org.dedda.copycat.database.Server

@Composable
fun HomeContents(
    repo: Repository
) {
    val servers = repo.allServers()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(servers) { server ->
                QuickRxTxListItem(server)
            }
        }
    }
}

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
                        text = "Send",
                    )
                }
                TextButton(onClick = {
                    receiveClipboard(localContext, server)
                }) {
                    Text(
                        color = appColors().receiveButtonColor,
                        text = "Receive",
                    )
                }
            }
        }
    }
}

fun sendClipboard(context: Context, server: Server) {
    val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    val primaryClip = clipboard.primaryClip
    runBlocking {
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
    }
}

fun receiveClipboard(context: Context, server: Server) {
    val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    val source = HttpClipboardSource(server)
    val text = source.requestText()
    if (text != null) {
        clipboard.setPrimaryClip(ClipData.newPlainText("CopyCat Paste", text))
        Toast.makeText(context, "Received clipboard from ${server.name}", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(
            context,
            "Could not request clipboard from ${server.name}",
            Toast.LENGTH_SHORT
        ).show()
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun QuickRxTxListItemPreview() {
    MyApplicationTheme {
        QuickRxTxListItem(server = SampleRepository().allServers()[0])
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomePreview() {
    MyApplicationTheme {
        HomeContents(repo = SampleRepository())
    }
}