package org.dedda.copycat.android

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColors(
            primary = Color(0xFFBB86FC),
            primaryVariant = Color(0xFF3700B3),
            secondary = Color(0xFF03DAC5)
        )
    } else {
        lightColors(
            primary = Color(0xFF6200EE),
            primaryVariant = Color(0xFF3700B3),
            secondary = Color(0xFF03DAC5)
        )
    }
    val typography = Typography(
        body1 = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = appColors().textColor,
        )
    )
    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp)
    )

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}

@Composable
fun appColors(): AppColors = if (isSystemInDarkTheme()) {
        AppColors(
            sendButtonColor = Color(0xFFDD5555),
            receiveButtonColor = Color(0xFF66DD66),
            editIconColor = Color(0xFF44AAFF),
            textColor = Color(0xFFD0D0D0),
            navBarLabelColor = Color(0xFFE0E0E0),
        )
    } else {
        AppColors(
            sendButtonColor = Color(0xFFAA3333),
            receiveButtonColor = Color(0xFF229922),
            editIconColor = Color(0xFF1133DD),
            textColor = Color(0xFF202020),
            navBarLabelColor = Color(0xFFE0E0E0),
        )
    }

data class AppColors(
    val sendButtonColor: Color,
    val receiveButtonColor: Color,
    val editIconColor: Color,
    val textColor: Color,
    val navBarLabelColor: Color,
)
