package co.edu.udea.compumovil.gr04_20262.teclado

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import co.edu.udea.compumovil.gr04_20262.teclado.ui.theme.TecladoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { TecladoTheme { RegistroScreen() } }
    }
}
