package org.dedda.copycat.android.home


import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.dedda.copycat.android.MyApplicationTheme
import org.dedda.copycat.android.sampledata.SampleRepository
import org.dedda.copycat.database.Repository

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
        ) {
            items(servers) { server ->
                QuickRxTxListItem(server)
            }
        }
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