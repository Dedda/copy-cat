package org.dedda.copycat.android

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import org.dedda.copycat.android.sampledata.SampleRepository
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
        LazyColumn() {
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
    Row(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = server.name)
        Row(
            modifier = Modifier.wrapContentSize(),
        ) {
            TextButton(onClick = {
                sendClipboard(localContext, server)
            }) {
                Text(text = "Send")
            }
            TextButton(onClick = {
                receiveClipboard(localContext, server)
            }) {
                Text(text = "Receive")
            }
        }
    }
}

fun sendClipboard(context: Context, server: Server) {
    Toast.makeText(context, "Sending Clipboard to ${server.name}...", Toast.LENGTH_SHORT).show()
}

fun receiveClipboard(context: Context, server: Server) {
    Toast.makeText(context, "Requesting Clipboard from ${server.name}...", Toast.LENGTH_SHORT).show()
}

@Preview
@Composable
fun LightQuickRxTxListItemPreview() {
    MyApplicationTheme(darkTheme = true) {
        QuickRxTxListItem(server = SampleRepository().allServers()[0])
    }
}

@Preview
@Composable
fun DarkQuickRxTxListItemPreview() {
    MyApplicationTheme(darkTheme = false) {
        QuickRxTxListItem(server = SampleRepository().allServers()[0])
    }
}

@Preview
@Composable
fun LightHomePreview() {
    MyApplicationTheme(darkTheme = false) {
        HomeContents(repo = SampleRepository())
    }
}

@Preview
@Composable
fun DarkHomePreview() {
    MyApplicationTheme(darkTheme = true) {
        HomeContents(repo = SampleRepository())
    }
}
