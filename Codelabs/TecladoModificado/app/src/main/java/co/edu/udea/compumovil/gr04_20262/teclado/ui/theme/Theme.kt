package co.edu.udea.compumovil.gr04_20262.teclado.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta teal propia del grupo
private val Light = lightColorScheme(
    primary = Color(0xFF00695C),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB2DFDB),
    secondary = Color(0xFF455A64),
    background = Color(0xFFF7FAF9),
    surface = Color(0xFFF7FAF9),
)
private val Dark = darkColorScheme(
    primary = Color(0xFF4DB6AC),
    onPrimary = Color(0xFF00201C),
    primaryContainer = Color(0xFF004D40),
    secondary = Color(0xFFB0BEC5),
    background = Color(0xFF101414),
    surface = Color(0xFF101414),
)

@Composable
fun TecladoTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (dark) Dark else Light, content = content)
}
