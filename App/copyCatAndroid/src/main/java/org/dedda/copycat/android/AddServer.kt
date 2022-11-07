package org.dedda.copycat.android

import android.content.res.Configuration
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.dedda.copycat.android.sampledata.SampleRepository
import org.dedda.copycat.database.Repository

@Composable
fun AddServerContents(
    repo: Repository,
    onNavigateBack: () -> Unit = {},
) {
    TextButton(onClick = onNavigateBack) {
        Text(text = "Back")
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
