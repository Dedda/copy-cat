package org.dedda.copycat.android

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.dedda.copycat.android.sampledata.SampleRepository
import org.dedda.copycat.database.Repository
import org.dedda.copycat.database.Server

@Composable
fun ServerListContents(
    repo: Repository,
    onNavigateToAddServer: () -> Unit = {},
    onNavigateToEditServer: (Long) -> Unit = {},
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(repo.allServers()) { server ->
                ServerListItem(
                    server = server,
                    onNavigateToEditServer = onNavigateToEditServer,
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

@Composable
fun ServerListItem(
    server: Server,
    onNavigateToEditServer: (Long) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                fontSize = 24.sp,
                text = server.name,
            )
            IconButton(onClick = { onNavigateToEditServer(server.id) }) {
                Icon(Icons.Filled.Edit, "Edit icon", tint = appColors().editIconColor)
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ServerListItemPreview() {
    MyApplicationTheme() {
        ServerListItem(SampleRepository().allServers()[0])
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ServerListPreview() {
    MyApplicationTheme() {
        ServerListContents(SampleRepository())
    }
}
