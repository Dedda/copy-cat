package org.dedda.copycat.android.serverlist

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.dedda.copycat.android.MyApplicationTheme
import org.dedda.copycat.android.appColors
import org.dedda.copycat.android.sampledata.SampleRepository
import org.dedda.copycat.database.Server

@Composable
fun ServerListItem(
    server: Server,
    onNavigateToEditServer: (Long) -> Unit = {},
    onDeleteServer: (Long) -> Unit = {},
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
            Row {
                IconButton(onClick = { onNavigateToEditServer(server.id) }) {
                    Icon(Icons.Filled.Edit, "Edit icon", tint = appColors().editIconColor)
                }
                IconButton(onClick = { onDeleteServer(server.id) }) {
                    Icon(Icons.Filled.Delete, "Delete icon", tint = appColors().deleteIconColor)
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ServerListItemPreview() {
    MyApplicationTheme {
        ServerListItem(SampleRepository().allServers()[0])
    }
}
