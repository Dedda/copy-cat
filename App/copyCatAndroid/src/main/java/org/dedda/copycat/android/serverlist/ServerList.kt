package org.dedda.copycat.android.serverlist

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.dedda.copycat.android.MyApplicationTheme
import org.dedda.copycat.android.sampledata.SampleRepository
import org.dedda.copycat.database.Repository

@Composable
fun ServerListContents(
    repo: Repository,
    onNavigateToAddServer: () -> Unit = {},
    onNavigateToEditServer: (Long) -> Unit = {},
) {
    var servers by remember { mutableStateOf(repo.allServers()) }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            items(servers) { server ->
                ServerListItem(
                    server = server,
                    onNavigateToEditServer = onNavigateToEditServer,
                    onDeleteServer = {
                        repo.deleteServer(server.id)
                        servers = repo.allServers()
                    },
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            FloatingActionButton(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomEnd),
                onClick = onNavigateToAddServer,
            ) {
                Icon(Icons.Filled.Add, "")
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ServerListPreview() {
    MyApplicationTheme {
        ServerListContents(SampleRepository())
    }
}
