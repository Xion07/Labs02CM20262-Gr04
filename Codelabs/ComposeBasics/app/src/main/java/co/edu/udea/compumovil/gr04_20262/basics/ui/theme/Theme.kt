package co.edu.udea.compumovil.gr04_20262.basics.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Blue,
    secondary = Chartreuse,
    background = Navy,
    surface = Navy,
    onPrimary = Navy,
    onBackground = Chartreuse,
    onSurface = Chartreuse
)

private val LightColorScheme = lightColorScheme(
    primary = Navy,
    secondary = Blue,
    primaryContainer = LightBlue,
    background = Chartreuse,
    surface = Chartreuse,
    onPrimary = Chartreuse,
    onBackground = Navy,
    onSurface = Navy
)

@Composable
fun BasicsCodelabTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
