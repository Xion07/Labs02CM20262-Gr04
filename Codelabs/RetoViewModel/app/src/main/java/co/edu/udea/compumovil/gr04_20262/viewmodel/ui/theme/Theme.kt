package co.edu.udea.compumovil.gr04_20262.viewmodel.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF006A6A),
    secondary = Color(0xFF4A6363),
    tertiary = Color(0xFF4B607C)
)
private val DarkColors = darkColorScheme(
    primary = Color(0xFF4CDADA),
    secondary = Color(0xFFB0CCCB),
    tertiary = Color(0xFFB3C8E8)
)

@Composable
fun UnscrambleTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
