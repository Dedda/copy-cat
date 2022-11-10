package org.dedda.copycat.android.addserver

import android.content.res.Configuration
import android.widget.Toast
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.dedda.copycat.android.MyApplicationTheme
import org.dedda.copycat.android.R
import org.dedda.copycat.android.appColors
import org.dedda.copycat.android.sampledata.SampleRepository
import org.dedda.copycat.database.Repository

@Composable
fun AddServerContents(
    repo: Repository,
    startAddress: String? = null,
    onNavigateBack: () -> Unit = {},
) {
    var serverName by remember { mutableStateOf("") }
    var serverAddress by remember { mutableStateOf(startAddress ?: "") }
    val context = LocalContext.current
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
                    if (serverName.isBlank()) {
                        Toast.makeText(context, R.string.add_server_toast_invalid_name, Toast.LENGTH_SHORT).show()
                        return@IconButton
                    }
                    if (serverAddress.isBlank()) {
                        Toast.makeText(context, R.string.add_server_toast_invalid_address, Toast.LENGTH_SHORT).show()
                        return@IconButton
                    }
                    repo.insertServer(serverName, serverAddress)
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
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                value = serverName,
                onValueChange = { serverName = it },
                label = {
                    Text(
                        text = stringResource(R.string.add_server_name_label),
                    )
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                value = serverAddress,
                onValueChange = { serverAddress = it },
                label = {
                    Text(
                        text = stringResource(R.string.add_server_address_label),
                    )
                }
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LightAddServerPreview() {
    MyApplicationTheme {
        AddServerContents(repo = SampleRepository())
    }
}
