package org.dedda.copycat.android.editserver

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.dedda.copycat.android.MyApplicationTheme
import org.dedda.copycat.android.R
import org.dedda.copycat.android.appColors
import org.dedda.copycat.android.sampledata.SampleRepository
import org.dedda.copycat.database.Repository
import org.dedda.copycat.database.Server

@Composable
fun EditServer(
    repo: Repository,
    serverId: Long,
    onNavigateBack: () -> Unit = {}
) {
    val server by remember { mutableStateOf(repo.serverById(serverId)) }
    if (server == null) {
        onNavigateBack()
        return
    }
    var serverName by remember { mutableStateOf(server!!.name) }
    var serverAddress by remember { mutableStateOf(server!!.address) }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.End,
            ) {
                IconButton(onClick = {
                    val newServer = Server(serverId, serverName, serverAddress)
                    repo.updateServer(newServer)
                    onNavigateBack()
                }) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "Save icon",
                        tint = appColors().saveIconColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.wrapContentSize(),
                    text = stringResource(R.string.edit_server_name_label),
                )
                Spacer(modifier = Modifier.width(16.dp))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    value = serverName,
                    onValueChange = { serverName = it },
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.wrapContentSize(),
                    text = stringResource(R.string.edit_server_address_label),
                )
                Spacer(modifier = Modifier.width(16.dp))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    value = serverAddress,
                    onValueChange = { serverAddress = it },
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun EditServerPreview() {
    MyApplicationTheme {
        EditServer(SampleRepository(), 1)
    }
}