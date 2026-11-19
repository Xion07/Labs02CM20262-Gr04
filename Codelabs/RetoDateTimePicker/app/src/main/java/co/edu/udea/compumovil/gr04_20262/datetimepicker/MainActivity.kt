package co.edu.udea.compumovil.gr04_20262.datetimepicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import co.edu.udea.compumovil.gr04_20262.datetimepicker.ui.theme.DateTimePickerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DateTimePickerTheme { AppointmentScreen() }
        }
    }
}
