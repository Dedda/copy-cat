package org.dedda.copycat.android

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
@Composable
fun LightAddServerPreview() {
    MyApplicationTheme(darkTheme = false) {
        AddServerContents(repo = SampleRepository())
    }
}

@Preview
@Composable
fun DarkAddServerPreview() {
    MyApplicationTheme(darkTheme = true) {
        AddServerContents(repo = SampleRepository())
    }
}